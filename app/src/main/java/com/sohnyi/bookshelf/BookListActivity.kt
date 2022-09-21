package com.sohnyi.bookshelf

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sohnyi.bookshelf.adapter.BookListAdapter
import com.sohnyi.bookshelf.databinding.ActivityBookListBinding

/**
 *
 * Create by yi on Thu 2022/09/22
 */
class BookListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookListBinding

    private val bookList: ArrayList<DoubanBookInfo> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvBook.layoutManager = LinearLayoutManager(this)
        binding.rvBook.adapter = BookListAdapter()
    }
}