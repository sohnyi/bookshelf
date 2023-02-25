package com.sohnyi.bookshelf.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sohnyi.bookshelf.entry.BookInfo

/**
 *
 * Create by yi on Fri 2022/09/23
 */
//private const val OLD_DATABASE_VERSION = 2
private const val DATABASE_VERSION = 2

@Database(
    entities = [BookInfo::class],
    version = DATABASE_VERSION,
//    autoMigrations = [
//        AutoMigration(from = OLD_DATABASE_VERSION, to = DATABASE_VERSION)
//    ]
)
abstract class BookShelfDatabase : RoomDatabase() {
    abstract fun bookInfoDao(): BookInfoDao
}