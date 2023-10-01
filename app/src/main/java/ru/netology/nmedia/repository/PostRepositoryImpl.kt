package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class PostRepositoryImpl(
    private val dao: PostDao
) : PostRepository {

    override val data = dao.getAll()
        .map {
            it.map(PostEntity::toDto)
        }
        .flowOn(Dispatchers.Default)

    override fun getNewerCount(postId: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = PostApi.service.getNewer(postId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            //  emit(body.size)
            emit(dao.unreadCount())

            dao.insert(body.toEntity().map {
                it.copy(hidden = true)
            })
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        val response = PostApi.service.getAll()
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val posts = response.body() ?: throw ApiError(response.code(), response.message())
        dao.removeAll()
        dao.insert(posts.map(PostEntity::fromDto))
    }

    override suspend fun readAll() {
        dao.readAll()
    }

    override suspend fun removeById(id: Long) {

        val post = dao.searchPost(id)
        try {
            dao.removeById(id)
            val response = PostApi.service.deletePost(post.id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            dao.insert(post)
            throw NetworkError
        } catch (e: Exception) {
            dao.insert(post)
            throw UnknownError
        }
    }

//    override suspend fun save(post: Post) {
//        try {
//            val response = PostApi.service.savePost(post)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            dao.insert(PostEntity.fromDto(body))
//        } catch (e: IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
//    }

    override suspend fun save(post: Post) {
        try {
            post.unposted = 1
            //  val maxId = dao.maxId().toLong()
            dao.insert(PostEntity.fromDto(post))
            val response = PostApi.service.savePost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.removeById(post.id)
            dao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun send(post: Post) {
        try {
            val response = PostApi.service.savePost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.removeById(post.localId)
            dao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(post: Post) {
        try {
            if (post.unposted == 0) {
                dao.likedById(post.id)
            }
            val response = when (post.likedByMe) {
                true -> {
                    PostApi.service.unlikePost(post.id)
                }

                false -> {
                    PostApi.service.likePost(post.id)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun shareById(id: Long) {
        dao.shareById(id)
        // на сервере нет подобной функции
    }

    override suspend fun viewById(id: Long) {
        dao.viewById(id)
        // на сервере нет подобной функции
    }
}