package com.sohnyi.bookshelf.adapter

import androidx.recyclerview.widget.DiffUtil
import com.sohnyi.bookshelf.DoubanBookInfo

/**
 *
 * Create by yi on Thu 2022/09/22
 */
object DoubanBookDiffCallback : DiffUtil.ItemCallback<DoubanBookInfo>() {

    override fun areItemsTheSame(oldItem: DoubanBookInfo, newItem: DoubanBookInfo): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DoubanBookInfo, newItem: DoubanBookInfo): Boolean {
        return oldItem.id == newItem.id
    }
}