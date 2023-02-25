package com.sohnyi.bookshelf.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 书籍信息
 */

@Entity(tableName = "book_info")
data class BookInfo(
    @PrimaryKey val isbn: String,
    var title: String? = null,
    var subtitle: String? = null,
    var image: String? = null,
    var author: String? = null,
    var translator: String? = null,
    @ColumnInfo(name = "original_title") var originalTitle: String? = null,
    var url: String? = null,
    @ColumnInfo(name = "publish_data") var publishDate: String? = null,
    var type: String? = null,
    var price: String? = null,
    var publisher: String? = null,
    var description: String? = null,
    var format: String? = null,
    var pages: String? = null,
    @ColumnInfo(name = "last_opened_time")
    var lastOpenedTime: Long? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookInfo

        if (isbn != other.isbn) return false
        if (title != other.title) return false
        if (subtitle != other.subtitle) return false
        if (image != other.image) return false
        if (author != other.author) return false
        if (translator != other.translator) return false
        if (originalTitle != other.originalTitle) return false
        if (url != other.url) return false
        if (publishDate != other.publishDate) return false
        if (type != other.type) return false
        if (price != other.price) return false
        if (publisher != other.publisher) return false
        if (description != other.description) return false
        if (format != other.format) return false
        if (pages != other.pages) return false
        if (lastOpenedTime != other.lastOpenedTime) return false

        return true
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}