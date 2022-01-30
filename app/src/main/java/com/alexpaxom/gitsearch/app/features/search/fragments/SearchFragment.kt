package com.alexpaxom.gitsearch.app.features.search.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexpaxom.gitsearch.R
import com.alexpaxom.gitsearch.app.App
import com.alexpaxom.gitsearch.app.features.mainwindow.viewmodel.MainActivityViewModel
import com.alexpaxom.gitsearch.app.features.repositorydetails.fragments.RepositoryDetailsFragment
import com.alexpaxom.gitsearch.app.features.search.adapters.SearchListAdapter
import com.alexpaxom.gitsearch.app.features.search.adapters.SearchListFactory
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchEffect
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchEvent
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchState
import com.alexpaxom.gitsearch.app.features.search.viewmodel.SearchFragmentViewModel
import com.alexpaxom.gitsearch.databinding.FragmentSearchBinding
import com.alexpaxom.homework_2.app.features.baseelements.adapters.PagingRecyclerUtil
import com.google.android.material.snackbar.Snackbar
import java.text.FieldPosition
import javax.inject.Inject

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchListFactory: SearchListFactory = SearchListFactory { onItemClick(it) }
    private val searchListAdapter: SearchListAdapter = SearchListAdapter(searchListFactory)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val searchFragmentViewModel = lazy {
        ViewModelProvider(this, viewModelFactory).get(SearchFragmentViewModel::class.java)
    }

    private val mainActivityViewModel = lazy {
        ViewModelProvider(requireActivity(), viewModelFactory).get(MainActivityViewModel::class.java)
    }

    private val searchPaging = lazy {
        SearchPaging(binding.rwSearchList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent
            .getScreenComponent()
            .create()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)

        binding.rwSearchList.layoutManager = LinearLayoutManager(context)
        binding.rwSearchList.adapter = searchListAdapter

        //Иннициализируем пагинацию
        searchPaging.value

        searchFragmentViewModel.value.viewState.observe(
            viewLifecycleOwner,
            { redrawState(it) }
        )

        mainActivityViewModel.value.internetConnection.observe(
            viewLifecycleOwner,
            {
                searchFragmentViewModel.value.processEvent(
                    SearchEvent.InternetConnectionEvent(it)
                )
            }
        )

        searchFragmentViewModel.value.effect.observe(
            viewLifecycleOwner,
            {  lifeDataEvent ->
                lifeDataEvent.getContentIfNotHandled()?.let { effect ->
                    processEffect(effect)
                }
            }
        )

        binding.bthSearch.setOnClickListener {
            searchFragmentViewModel.value.processEvent(
                SearchEvent.Search(binding.editSearchField.text.toString())
            )
        }

        return binding.root
    }

    private fun redrawState(state: SearchState) {
        searchListAdapter.dataList = state.searchResultList
        binding.progressBarSearch.isVisible = state.isEmptyLoading
        binding.loadNextPageProgress.isVisible = state.isNextPageLoading
    }

    private fun onItemClick(position: Int) {
        val repositoryDetailsFragment =
            RepositoryDetailsFragment.newInstance(searchListAdapter.dataList[position])

        parentFragmentManager.beginTransaction()
            .replace(
                R.id.main_fragment_container,
                repositoryDetailsFragment,
                RepositoryDetailsFragment.FRAGMENT_ID
            )
            .addToBackStack(RepositoryDetailsFragment.FRAGMENT_ID)
            .commit()
    }

    private fun processEffect(effect: SearchEffect) {
        when(effect) {
            is SearchEffect.ShowError -> showError(effect.error)
        }
    }

    private fun showError(error: String) {
        Snackbar.make(binding.root, error, Snackbar.LENGTH_INDEFINITE)
            .setAction("OK") {}
            .show()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class SearchPaging(searchRecycler: RecyclerView) :
        PagingRecyclerUtil(searchRecycler) {
        override fun checkLoadData(bottomPos: Int, topPos: Int) {
            if (SearchFragmentViewModel.COUNT_ELEMENTS_BEFORE_START_LOAD
                >
                (searchListAdapter.dataList.size - bottomPos)) {
                    searchFragmentViewModel.value.processEvent(
                        SearchEvent.LoadNextPage()
                    )
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}