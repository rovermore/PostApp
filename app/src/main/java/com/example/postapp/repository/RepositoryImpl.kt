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
            saveAllPosts(posts)
            posts
        }
    }

    private suspend fun saveAllPosts(posts: List<Post>?) {
        posts?.let {
            for (post in posts) {
                roomDataSource.savePost(post)
            }
        }
    }

    override suspend fun getPostById(id: Int): Post? {
        val postFromDataBase = roomDataSource.getPostById(id)
        return if (postFromDataBase != null) {
            postFromDataBase
        } else {
            val post = retrofitDataSource.getPostById(id)
            savePost(post)
            post
        }
    }

    private suspend fun savePost(post: Post?){
        post?.let { roomDataSource.savePost(post) }

    }

    override suspend fun getCommentsFromPostId(id: Int): List<Comment>? {
        val commentListFromDB = roomDataSource.getCommentsFromPost(id)
        return if (!commentListFromDB.isNullOrEmpty()) {
            commentListFromDB
        } else {
            val comments = retrofitDataSource.getCommentsByPostId(id)
            saveAllComments(comments)
            comments
        }
    }

    private suspend fun saveAllComments(comments: List<Comment>?) {
        comments?.let {
            for (comment in comments) {
                roomDataSource.saveComment(comment)
            }
        }
    }

    override suspend fun getUserById(id: Int?): User? {
        id?.let {
            val userFromDB = roomDataSource.getUserById(id)
            return if (userFromDB != null) {
                userFromDB
            } else {
                val user = retrofitDataSource.getUserById(id)
                saveUser(user)
                user
            }
        }
        return null
    }

    private suspend fun saveUser(user: User?) {
        user?.let { roomDataSource.insertUser(user) }
    }
}