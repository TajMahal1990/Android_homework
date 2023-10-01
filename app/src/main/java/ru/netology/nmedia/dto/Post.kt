package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val localId: Long,
    var unposted: Int = 0,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val hidden: Boolean = false,
    val shareCount: Int = 0,
    val viewsCount: Int = 0,
    val likedByMe: Boolean,
    val linkVideo: String? = null,
    val attachment: Attachment? = null,
)

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
)