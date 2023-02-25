package com.sohnyi.bookshelf

import android.app.Application
import com.sohnyi.bookshelf.database.BookInfoRepository

/**
 *
 * Create by yi on Fri 2022/09/23
 */
class BookShelfApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Thread {
            BookInfoRepository.initialize(applicationContext)
        }.start()
    }
}