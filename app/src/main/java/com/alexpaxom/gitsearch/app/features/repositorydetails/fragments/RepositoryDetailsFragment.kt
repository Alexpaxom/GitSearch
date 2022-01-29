package com.alexpaxom.gitsearch.app.features.repositorydetails.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsEvent
import com.alexpaxom.gitsearch.app.features.repositorydetails.elementsofstate.RepositoryDetailsState
import com.alexpaxom.gitsearch.app.features.repositorydetails.viewmodel.RepositoryDetailsViewModel
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchState
import com.alexpaxom.gitsearch.app.features.search.viewmodel.SearchFragmentViewModel
import com.alexpaxom.gitsearch.databinding.FragmentRepositoryDetailsBinding
import com.alexpaxom.gitsearch.databinding.FragmentSearchBinding
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop

class RepositoryDetailsFragment : Fragment() {
    private var repositoryInfo: RepositoryCard? = null
    private var _binding: FragmentRepositoryDetailsBinding? = null
    private val binding get() = _binding!!

    private val repositoryDetailsViewModel = lazy {
        ViewModelProvider(this).get(RepositoryDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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

       repositoryDetailsViewModel.value.viewState.observe(
           viewLifecycleOwner,
            { redrawState(it) }
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repositoryDetailsViewModel.value.processEvent(
            RepositoryDetailsEvent.GetRepositoryDetails(repositoryInfo ?: error("Bad param repository info"))
        )
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
            .transform(
                CenterCrop(),
                //RoundedCorners(this.resources.getDimensionPixelOffset(R.dimen.profile_avatar_rounded_corners))
                )
            .into(binding.ownerAvatar)
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