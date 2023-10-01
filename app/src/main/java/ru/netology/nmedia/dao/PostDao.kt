package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity WHERE hidden == 0 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE localId = :localId")
    suspend fun searchPost(localId: Long): PostEntity

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun searchPostById(id: Long): PostEntity

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT MAX(id) FROM PostEntity")
    suspend fun maxId(): Int

    @Query("SELECT COUNT(*) FROM PostEntity WHERE hidden == 1")
    suspend fun unreadCount(): Int

    @Query("SELECT COUNT(*) FROM PostEntity WHERE hidden == 1")
    fun observeCount(): Flow<Int>

    @Query("UPDATE PostEntity SET hidden = 0")
    suspend fun readAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    suspend fun likedById(id: Long)

    @Query(
        """
            UPDATE PostEntity SET
            shareCount = shareCount + 1
             WHERE id = :id;
        """
    )
    suspend fun shareById(id: Long)

    @Query(
        """
            UPDATE PostEntity SET
            viewsCount = viewsCount + 1
             WHERE id = :id;
        """
    )
    suspend fun viewById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)


    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()

}