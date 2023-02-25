package com.sohnyi.bookshelf

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sohnyi.bookshelf.adapter.BookListAdapter
import com.sohnyi.bookshelf.database.BookInfoRepository
import com.sohnyi.bookshelf.databinding.ActivityBookListBinding
import com.sohnyi.bookshelf.entry.BookInfo
import com.sohnyi.bookshelf.utils.setStatusBarMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * Create by yi on Thu 2022/09/22
 */
class BookListActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityBookListBinding

    private val mBookList: ArrayList<BookInfo> = arrayListOf()

    companion object {

        private const val RESULT_EXTRA_ISBN = "com.sohnyi.bookshelf.result.isbn"
        fun getIsbnResult(data: Intent): String? {
            return data.getStringExtra(RESULT_EXTRA_ISBN)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBookListBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setStatusBarMode(Color.WHITE, true)

        mBinding.rvBook.layoutManager = LinearLayoutManager(this)
        val adapter = BookListAdapter(::onItemClick)
        mBinding.rvBook.adapter = adapter

        // 加载所有书籍
        lifecycleScope.launch(Dispatchers.IO) {
            mBinding.progressCircular.visibility = View.VISIBLE
            mBookList.addAll(BookInfoRepository.get().getAllBooks())
            withContext(Dispatchers.Main) {
                mBinding.progressCircular.visibility = View.GONE
                adapter.submitList(mBookList)
            }
        }
    }

    /**
     * 书籍信息条目信息点击事件
     *
     * @param isbn 点击的书籍的isbn
     */
    private fun onItemClick(isbn: String) {
        val result = Intent().apply {
            setIsbnResult(this, isbn)
        }
        setResult(Activity.RESULT_OK, result)
        finishAndRemoveTask()
    }

    /**
     * 设置返回的 isbn 结果
     *
     * @param data 返回结果值
     */
    private fun setIsbnResult(data: Intent, isbn: String) {
        data.putExtra(RESULT_EXTRA_ISBN, isbn)
    }
}