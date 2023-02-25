package com.sohnyi.bookshelf.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.sohnyi.bookshelf.database.BookInfoRepository
import com.sohnyi.bookshelf.entry.BookInfo
import kotlinx.coroutines.*

/**
 *
 * Create by yi on Fri 2022/09/23
 */
class BookInfoViewModel(application: Application) : AndroidViewModel(application),
    DefaultLifecycleObserver {

    private val bookList by lazy {
        mutableListOf<BookInfo>()
    }


    private val repository by lazy {
        BookInfoRepository.get()
    }

    private val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    init {
        scope.launch {
            bookList.addAll(repository.getAllBooks())
        }
    }

    fun getBooks(): List<BookInfo> {
        return bookList
    }

    suspend fun getBook(isbn: String): BookInfo = repository.getBook(isbn)

    fun addBook(bookInfo: BookInfo) {
        scope.launch {
            repository.addBook(bookInfo)
        }
    }

    fun deleteBook(bookInfo: BookInfo) {
        scope.launch {
            repository.deleteBook(bookInfo)
        }
    }

    fun updateBook(bookInfo: BookInfo) {
        scope.launch {
            repository.updateBook(bookInfo)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

    }
}