package com.sohnyi.bookshelf.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sohnyi.bookshelf.R
import com.sohnyi.bookshelf.databinding.ItemBookBinding
import com.sohnyi.bookshelf.entry.BookInfo

/**
 *
 * Create by yi on Thu 2022/09/22
 */
class BookListAdapter(private val onItemClick: (isbn: String) -> Unit) :
    ListAdapter<BookInfo, BookListAdapter.BookListViewHolder>(
        BookDiffCallback
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        with(holder) {
            val info = getItem(position)
            info.image?.let {
                if (it.endsWith("jpg")) {
                    val url = it.substring(0, it.lastIndexOf(".")) + ".jpeg"
                    binding.ivCover.load(url) {
                        crossfade(true)
                    }
                } else {
                    binding.ivCover.load(it) {
                        crossfade(true)
                    }
                }
            }
            binding.tvTitle.text = info.title ?: ""
            binding.tvAuthor.text = info.author ?: ""
            binding.tvPublish.text =
                binding.tvPublish.context.getString(
                    R.string.dis_item_publish, info.publisher, info.publishDate, info.price
                )
            itemView.setOnClickListener { onItemClick(info.isbn) }
        }
    }

    class BookListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemBookBinding.bind(itemView)
    }
}