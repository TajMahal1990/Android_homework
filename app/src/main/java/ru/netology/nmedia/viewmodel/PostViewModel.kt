package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent


private val empty = Post(
    id = 0,
    localId = 0,
    content = "",
    author = "",
    authorAvatar = "",
    published = "",
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryImpl(AppDb.getInstance(application).postDao())
    val data: LiveData<FeedModel> = repository.data
        .map {
            FeedModel(it, it.isEmpty())
        }
        .asLiveData(Dispatchers.Default)

    val newerCount: LiveData<Int> = data.switchMap {
        val firstId = it.posts.firstOrNull()?.id ?: 0L
        repository.getNewerCount(firstId).asLiveData(Dispatchers.Default)
    }

    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state
    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.value = FeedModelState(loading = true)
            _state.value = try {
                repository.getAll()
                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }
    }

    fun refreshPosts() {
        viewModelScope.launch {
            _state.value = FeedModelState(refreshing = true)
            _state.value = try {
                repository.getAll()
                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }
    }

    fun readAll() =
        viewModelScope.launch {
            _state.value = FeedModelState(refreshing = true)
            _state.value = try {
                repository.readAll()
                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }

    fun likeById(post: Post) {
        viewModelScope.launch {
            _state.value = FeedModelState(refreshing = true)
            _state.value = try {
                repository.likeById(post)
                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }
    }

        fun shareById(id: Long) {
            viewModelScope.launch {
                _state.value = FeedModelState(refreshing = true)
                repository.shareById(id)
            }
        }

        fun viewById(id: Long) {
            viewModelScope.launch {
                _state.value = FeedModelState(refreshing = true)
                repository.viewById(id)
            }
        }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _state.value = FeedModelState()

                } catch (e: Exception) {
                    _state.value = FeedModelState(error = true)
                }
            }

        }
        clear()
    }

    fun send(post: Post) {
        viewModelScope.launch {
            _state.value = try {
                repository.send(post)
                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content != text) {
            edited.value = edited.value?.copy(content = text)
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            _state.value = FeedModelState(refreshing = true)
            _state.value = try {
                repository.removeById(id)
                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }

//        _data.value = FeedModel(refreshing = true)
//        repository.removeById(id, object : PostRepository.Callback<Unit> {
//            val oldPosts = _data.value
//            override fun onSuccess(posts: Unit) {
//                _data.value =
//                    oldPosts?.copy(
//                        posts = oldPosts.posts.filter {
//                            it.id != id
//                        }
//                    )
//            }
//
//            override fun onError(e: Exception) {
//                toastServerError.show()
//                _data.value = oldPosts
//                _data.value = FeedModel(error = true)
//            }
//        })
    }

    fun clear() {
        edited.value = empty
    }
}