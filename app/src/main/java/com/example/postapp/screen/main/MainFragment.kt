package com.example.postapp.screen.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.postapp.PostApp
import com.example.postapp.R
import com.example.postapp.databinding.FragmentMainBinding
import com.example.postapp.model.canon.Post
import com.example.postapp.utils.ViewState
import com.example.postapp.utils.gone
import com.example.postapp.utils.visible
import javax.inject.Inject


class MainFragment : Fragment(), MainAdapter.OnItemClicked{

    @Inject
    lateinit var mainViewModel: MainViewModel

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var adapter = MainAdapter(null, this)
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        layoutManager = LinearLayoutManager(requireContext())
        setupObservers()
        return binding.root
    }

    init { PostApp.daggerAppComponent().inject(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupReloadButton() {
        binding.errorView.reloadButton.setOnClickListener {
            adapter.clearMainAdapter()
        }
    }

    private fun setupObservers() {
        observeState()
    }

    private fun observeState() {
        mainViewModel.viewState.observe(viewLifecycleOwner, Observer {
            updateUI(it)
        })
    }

    private fun setupUI() {
        setUpRecyclerView()
        setupReloadButton()
    }

    private fun setUpRecyclerView() {
        binding.mainRecycler.apply {
            layoutManager = layoutManager
            setHasFixedSize(true)
            adapter = adapter
        }
    }

    private fun updateUI(viewState: ViewState) {
        when (viewState) {
            ViewState.Error -> setErrorView()
            ViewState.Loading -> setLoadingView()
            is ViewState.Content<*> -> {
                setSuccessView()
                updateAdapter(viewState.data as List<Post>)
            }
        }
    }

    private fun updateAdapter(postList: List<Post>) {
        adapter.updatePostList(postList.toMutableList())
    }

    private fun setErrorView() {
        binding.apply {
            mainRecycler.gone()
            progressBar.gone()
            errorView.apply {
                errorImage.visible()
                errorImage.bringToFront()
                errorText.visible()
                errorText.text = resources.getString(R.string.error_database)
                errorText.bringToFront()
            }
        }
    }

    private fun setLoadingView() {
        binding.apply {
            progressBar.apply {
                visible()
                bringToFront()
            }
        }
    }

    private fun setSuccessView() {
        binding.apply {
            progressBar.gone()
            mainRecycler.visible()
        }
    }

    override fun itemClicked(post: Post) {
        navigateToDetailFragment(post.id)
    }

    private fun navigateToDetailFragment(postId: Int) {
        val bundle = bundleOf("postId" to postId)
        NavHostFragment.findNavController(this).navigate(R.id.action_MainFragment_to_DetailFragment, bundle)
    }

}