package com.example.postapp.repository

import android.util.Log
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

    override suspend fun getAllPosts(): List<Post>? {
        val postsFromDataBase = roomDataSource.getAllPosts()
        return if (!postsFromDataBase.isNullOrEmpty()) {
            postsFromDataBase
        } else {
            val posts = retrofitDataSource.getAllPosts()
            posts?.let {
                for (post in posts) {
                    roomDataSource.savePost(post)
                }
            }
            posts
        }
    }

    override suspend fun getPostById(id: Int): Post? {
        val postFromDataBase = roomDataSource.getPostById(id)
        return if (postFromDataBase != null) {
            postFromDataBase
        } else {
            val post = retrofitDataSource.getPostById(id)
            post?.let { roomDataSource.savePost(post) }
            post
        }
    }

    override suspend fun getCommentsFromPostId(id: Int): List<Comment>? {
        val commentListFromDB = roomDataSource.getCommentsFromPost(id)
        return if (!commentListFromDB.isNullOrEmpty()) {
            commentListFromDB
        } else {
            val comments = retrofitDataSource.getCommentsByPostId(id)
            comments?.let {
                for (comment in comments) {
                    roomDataSource.saveComment(comment)
                }
            }
            comments
        }
    }

    override suspend fun getUserById(id: Int?): User? {
        id?.let {
            val userFromDB = roomDataSource.getUserById(id)
            return if (userFromDB != null) {
                userFromDB
            } else {
                val user = retrofitDataSource.getUserById(id)
                user?.let { roomDataSource.insertUser(user) }
                user
            }
        }
        return null
    }
}