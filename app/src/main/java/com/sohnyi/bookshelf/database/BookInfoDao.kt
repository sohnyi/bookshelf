package com.sohnyi.bookshelf.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Update
import com.sohnyi.bookshelf.entry.BookInfo

/**
 *
 * Create by yi on Fri 2022/09/23
 * 图书信息实体类
 */

@Dao
interface BookInfoDao {

    @Query("SELECT * FROM book_info ORDER BY last_opened_time DESC")
    suspend fun getAllBooks(): List<BookInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(bookInfo: BookInfo)

    @Delete
    suspend fun deleteBook(bookInfo: BookInfo)

    @Update
    suspend fun updateBook(bookInfo: BookInfo)

    @Query("SELECT * FROM book_info WHERE isbn LIKE :isbn")
    suspend fun getBook(isbn: String): BookInfo
}