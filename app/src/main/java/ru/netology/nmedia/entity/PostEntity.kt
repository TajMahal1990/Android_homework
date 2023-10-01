package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val localId: Long,
    val unposted: Int = 0,
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
) {
    fun toDto() =
        Post(id, localId, unposted, author, authorAvatar, content, published, likes, hidden, shareCount, viewsCount, likedByMe, linkVideo)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.localId,
                dto.unposted,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likes,
                dto.hidden,
                dto.shareCount,
                dto.viewsCount,
                dto.likedByMe,
                dto.linkVideo
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)