package com.alexpaxom.gitsearch.app.features.repositorydetails.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alexpaxom.gitsearch.app.App
import com.alexpaxom.gitsearch.app.features.mainwindow.viewmodel.MainActivityViewModel
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsEffect
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsEvent
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsState
import com.alexpaxom.gitsearch.app.features.repositorydetails.viewmodel.RepositoryDetailsViewModel
import com.alexpaxom.gitsearch.app.helpers.ErrorsHandler
import com.alexpaxom.gitsearch.databinding.FragmentRepositoryDetailsBinding
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class RepositoryDetailsFragment : Fragment() {
    private var repositoryInfo: RepositoryCard? = null
    private var _binding: FragmentRepositoryDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    private val repositoryDetailsViewModel = lazy {
        ViewModelProvider(this, viewModelFactory).get(RepositoryDetailsViewModel::class.java)
    }

    private val mainActivityViewModel = lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(MainActivityViewModel::class.java)
    }

    private val errorsHandler: ErrorsHandler = ErrorsHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent
            .getScreenComponent()
            .create()
            .inject(this)

        super.onCreate(savedInstanceState)
        arguments?.let {
            repositoryInfo = it.getParcelable(REPOSITORY_INFO_ARG_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepositoryDetailsBinding.inflate(inflater)

        binding.backToListBth.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reloadData()

        repositoryDetailsViewModel.value.viewState.observe(
            viewLifecycleOwner,
            { redrawState(it) }
        )

        repositoryDetailsViewModel.value.effect.observe(
            viewLifecycleOwner
        ) { liveDataEvent ->
            liveDataEvent.getContentIfNotHandled()?.let { effect ->
                processEffect(effect)
            }

        }

        mainActivityViewModel.value.internetConnection.observe(viewLifecycleOwner) {
            repositoryDetailsViewModel.value.processEvent(
                RepositoryDetailsEvent.InternetConnectionEvent(it)
            )
        }
    }

    private fun reloadData() {
        if (repositoryInfo == null)
            errorsHandler.processError(Throwable("Bad param repository info"))

        repositoryInfo?.let {
            repositoryDetailsViewModel.value.processEvent(
                RepositoryDetailsEvent.GetRepositoryDetails(it)
            )
        }
    }

    private fun redrawState(state: RepositoryDetailsState) {
        binding.loadInfoProgress.isVisible = state.isEmptyLoading
        binding.repositoryTitle.text = state.repositoryInfo.name
        binding.repositoryDescription.text = state.repositoryInfo.description
        binding.ownerName.text = state.repositoryOwnerInfo.name
        binding.ownerLogin.text = state.repositoryOwnerInfo.login
        binding.ownerEmail.text = state.repositoryOwnerInfo.email

        val glide = Glide.with(requireActivity())

        glide.load(state.repositoryOwnerInfo.avatarUrl)
            .into(binding.ownerAvatar)
    }

    private fun processEffect(effect: RepositoryDetailsEffect) {
        when (effect) {
            RepositoryDetailsEffect.ReloadData -> onReturnInternetConnection()
            is RepositoryDetailsEffect.ShowError -> showError(effect.error)
        }
    }

    private fun onReturnInternetConnection() {
        repositoryDetailsViewModel.value.viewState.value?.let {
            if (!it.dataIsLoaded)
                reloadData()
        }
    }

    private fun showError(error: String) {
        Snackbar.make(binding.root, error, Snackbar.LENGTH_INDEFINITE)
            .setAction("OK", {})
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val FRAGMENT_ID = "com.alexpaxom.gitsearch.REPOSITORY_DETAILS_FRAGMENT"

        private val REPOSITORY_INFO_ARG_PARAM = "com.alexpaxom.gitsearch.REPOSITORY_INFO_ARG_PARAM"

        @JvmStatic
        fun newInstance(repositoryInfo: RepositoryCard) =
            RepositoryDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(REPOSITORY_INFO_ARG_PARAM, repositoryInfo)
                }
            }
    }
}