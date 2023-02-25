package com.sohnyi.bookshelf.adapter

import androidx.recyclerview.widget.DiffUtil
import com.sohnyi.bookshelf.entry.BookInfo

/**
 *
 * Create by yi on Thu 2022/09/22
 */
object BookDiffCallback : DiffUtil.ItemCallback<BookInfo>() {

    override fun areItemsTheSame(oldItem: BookInfo, newItem: BookInfo): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BookInfo, newItem: BookInfo): Boolean {
        return oldItem.isbn == newItem.isbn
    }
}