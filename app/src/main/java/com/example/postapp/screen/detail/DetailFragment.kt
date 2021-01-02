package com.example.postapp.screen.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.postapp.PostApp
import com.example.postapp.databinding.FragmentDetailBinding
import com.example.postapp.model.canon.PostDetail
import com.example.postapp.utils.ViewState
import com.example.postapp.utils.gone
import com.example.postapp.utils.visible
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject


class DetailFragment : Fragment() {

    @Inject
    lateinit var detailViewModel: DetailViewModel

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private var adapter = CommentAdapter(null)
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        layoutManager = LinearLayoutManager(context)
        val bundle = arguments
        val postId = bundle?.getString("postId")
        postId?.let { detailViewModel.initialize(it.toInt()) }
        setupObservers()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    init {
        PostApp.daggerAppComponent().inject(this)
    }

    private fun setupReloadButton() {
        //binding.reloadButton.setOnClickListener { detailViewModel.loadData() }
    }

    private fun setupObservers() {
        observeState()
    }

    private fun observeState() {
        detailViewModel.viewState.observe(viewLifecycleOwner, Observer {
            updateUI(it)
        })
    }

    private fun setupUI() {
        setUpRecyclerView()
        setupReloadButton()
    }

    private fun setUpRecyclerView() {
        binding.commentsRecycler.apply {
            visibility = View.GONE
            layoutManager = layoutManager
            setHasFixedSize(true)
            adapter = adapter
        }
    }

    private fun updateUI(it: ViewState?) {
        when (it) {
            ViewState.Error -> setErrorView()
            ViewState.Loading -> setLoadingView()
            is ViewState.Content<*> -> {
                setSuccessView()
                updateAdapter(it.data as PostDetail)
            }
        }
    }

    private fun updateAdapter(postDetail: PostDetail) {
        adapter.updateCommentList(postDetail.commentList.toMutableList())
    }

    private fun setErrorView() {
        binding.apply {
            progressBar.gone()
            errorView.apply {
                errorImage.visible()
                errorText.visible()
                reloadButton.visible()
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
            commentsRecycler.visible()
        }
    }

}