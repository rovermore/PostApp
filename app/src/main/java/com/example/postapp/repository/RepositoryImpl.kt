package com.example.postapp.repository

import com.example.postapp.client.RetrofitDataSource
import com.example.postapp.db.RoomDataSource
import com.example.postapp.model.canon.Comment
import com.example.postapp.model.canon.Post
import com.example.postapp.model.canon.User
import java.lang.Exception
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val retrofitDataSource: RetrofitDataSource,
                                         private val roomDataSource: RoomDataSource
): Repository {

    override suspend fun getAllPosts(): List<Post> {
        val postsFromDataBase = roomDataSource.getAllPosts()
        return if (!postsFromDataBase.isNullOrEmpty()) {
            postsFromDataBase
        } else {
            val posts = retrofitDataSource.getAllPosts()
            for (post in posts) {
                roomDataSource.savePost(post)
            }
            posts
        }
    }

    override suspend fun getPostById(id: Int): Post {
        return try {
            roomDataSource.getPostById(id)
        } catch (e: Exception) {
            val post = retrofitDataSource.getPostById(id)
            roomDataSource.savePost(post)
            post
        }
    }

    override suspend fun getCommentsFromPostId(id: Int): List<Comment> {
        return try {
            roomDataSource.getCommentsFromPost(id)
        } catch (e: Exception) {
            val comments = retrofitDataSource.getCommentsByPostId(id)
            for (comment in comments) {
                roomDataSource.saveComment(comment)
            }
            comments
        }
    }

    override suspend fun getUserById(id: Int): User {
        return try {
            roomDataSource.getUserById(id)
        } catch (e: Exception) {
            val user = retrofitDataSource.getUserById(id)
            roomDataSource.insertUser(user)
            user
        }
    }
}