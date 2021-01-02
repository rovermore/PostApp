package com.example.postapp.usecase

import com.example.postapp.model.canon.PostDetail
import com.example.postapp.repository.RepositoryImpl
import javax.inject.Inject

class GetPostDetailsUseCase
@Inject constructor(private val repositoryImpl: RepositoryImpl): UseCaseWithParameter<PostDetail, Int> {

    override suspend fun requestWithParameter(p: Int): PostDetail =
        PostDetail(
            repositoryImpl.getPostById(p).title,
            repositoryImpl.getPostById(p).body,
            repositoryImpl.getUserById(p).userName,
            repositoryImpl.getCommentsFromPostId(p)
        )
}