package com.sohnyi.bookshelf.database

import android.content.Context
import androidx.room.Room
import com.sohnyi.bookshelf.entry.BookInfo
import com.sohnyi.bookshelf.utils.LogUtil

/**
 *
 * Create by yi on Fri 2022/09/23
 */
class BookInfoRepository private constructor(context: Context) {

    companion object {
        private const val TAG = "BookInfoRepository"
        const val DATABASE_NAME = "bookinfo-database"

        private var INSTANCE: BookInfoRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = BookInfoRepository(context)
            }
            LogUtil.i(TAG, "initialize(): BookInfoRepository initialized")
        }

        fun get(): BookInfoRepository {
            return INSTANCE ?: throw IllegalStateException("BookInfoRepository must be initialized")
        }
    }

    private val database: BookShelfDatabase = Room.databaseBuilder(
        context,
        BookShelfDatabase::class.java,
        DATABASE_NAME
    ).fallbackToDestructiveMigration()
        .build()

    private val bookInfoDao = database.bookInfoDao()

    suspend fun getAllBooks(): List<BookInfo> = bookInfoDao.getAllBooks()

    suspend fun getBook(isbn: String): BookInfo = bookInfoDao.getBook(isbn)

    suspend fun addBook(bookInfo: BookInfo) = bookInfoDao.insertBook(bookInfo)

    suspend fun deleteBook(bookInfo: BookInfo) = bookInfoDao.deleteBook(bookInfo)

    suspend fun updateBook(bookInfo: BookInfo) = bookInfoDao.updateBook(bookInfo)
}