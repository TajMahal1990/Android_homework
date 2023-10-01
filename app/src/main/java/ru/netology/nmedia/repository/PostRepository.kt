package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    fun getNewerCount(postId: Long): Flow<Int>
    suspend fun getAll()
    suspend fun readAll()
    suspend fun shareById(id: Long)
    suspend fun viewById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun likeById(post: Post)
    suspend fun send(post: Post)
}