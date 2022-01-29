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
import com.alexpaxom.gitsearch.app.features.search.adapters.SearchListAdapter
import com.alexpaxom.gitsearch.app.features.search.adapters.SearchListFactory
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchEvent
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchState
import com.alexpaxom.gitsearch.app.features.search.viewmodel.SearchFragmentViewModel
import com.alexpaxom.gitsearch.databinding.FragmentSearchBinding
import com.alexpaxom.homework_2.app.features.baseelements.adapters.PagingRecyclerUtil

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchListFactory: SearchListFactory = SearchListFactory()
    private val searchListAdapter: SearchListAdapter = SearchListAdapter(searchListFactory)
    private val searchFragmentViewModel = lazy {
        ViewModelProvider(this).get(SearchFragmentViewModel::class.java)
    }

    private val searchPaging = lazy {
        SearchPaging(binding.rwSearchList)
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
            getViewLifecycleOwner(),
            { redrawState(it) }
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class SearchPaging(private val searchRecycler: RecyclerView) :
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