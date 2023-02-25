package com.sohnyi.bookshelf

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.sohnyi.bookshelf.database.BookInfoRepository
import com.sohnyi.bookshelf.databinding.ActivityMainBinding
import com.sohnyi.bookshelf.entry.BookInfo
import com.sohnyi.bookshelf.model.RepoSubjectSuggest
import com.sohnyi.bookshelf.utils.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import okhttp3.Call
import okhttp3.Callback
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mBinding: ActivityMainBinding


    private val mBookList: MutableList<BookInfo> by lazy {
        mutableListOf()
    }
    private var mBookInfo: BookInfo? = null

    private val mBarcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            getUrlBodyByCode(result.contents)
            mBinding.progressBar.visibility = View.VISIBLE
        }
    }

    private val mOkHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                setLevel(HttpLoggingInterceptor.Level.NONE)
            }
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }


    private var mSubjectId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnScan.setOnClickListener { scanBarCode() }

        val bookListLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.let { data ->
                        BookListActivity.getIsbnResult(data)?.let { isbn ->
                            mBookInfo = mBookList.find { isbn == it.isbn }
                            mBookInfo?.let {
                                updateBookInfoUI(it)
                                it.lastOpenedTime = System.currentTimeMillis()
                                lifecycleScope.launch(Dispatchers.IO) {
                                    BookInfoRepository.get().updateBook(it)
                                }
                            }
                        }
                    }
                }
            }

        // 查看所有书目
        mBinding.ivList.setOnClickListener {
            bookListLauncher.launch(Intent(this, BookListActivity::class.java))
        }

        // 加载所有书目
        lifecycleScope.launch(Dispatchers.Main) {
            showLoading()
            val job = async(Dispatchers.IO) {
                mBookList.addAll(BookInfoRepository.get().getAllBooks())
            }
            job.await()
            withContext(Dispatchers.Main) {
                mBinding.progressBar.visibility = View.GONE
                if (mBookList.isNotEmpty()) {
                    updateBookInfoUI(mBookList[0])
                }
            }
        }

        // 添加到数据库
        mBinding.btnAdd.setOnClickListener {
            mBookInfo?.let { book ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val savedInfo = mBookList.find { book.isbn == it.isbn }
                    if (savedInfo == null) {
                        mBookInfo = book
                        BookInfoRepository.get().addBook(book)
                        LogUtil.d(TAG, "getInfoBySubjectId: add book ${book.isbn}")
                    } else if (savedInfo != book) {
                        BookInfoRepository.get().updateBook(book)
                        LogUtil.d(TAG, "getInfoBySubjectId: update book ${book.isbn}")
                    } else {
                        LogUtil.d(TAG, "getInfoBySubjectId: info is saved")
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showCloseDialog()
            }
        })
    }

    private fun scanBarCode() {
        val options = ScanOptions().apply {
            captureActivity = MyCaptureActivity::class.java
            setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
            setCameraId(0)
            setPrompt("scan")
            setBeepEnabled(false)
            setOrientationLocked(false)
            setBarcodeImageEnabled(true)
        }
        mBarcodeLauncher.launch(options)
    }

    private fun getUrlBodyByCode(code: String) {
        val httpUrl = HttpUrl.Builder()
            .scheme(Uri.parse(BASE_URL_DOUBAN_BOOK).scheme ?: "")
            .host(Uri.parse(BASE_URL_DOUBAN_BOOK).host ?: "")
            .addEncodedPathSegments(PATH_SUBJECT_SUGGEST)
            .addQueryParameter("q", code)
            .build()

        showLoading()
        Log.i(TAG, "getUrlBody: ${httpUrl.toUrl()}")
        val request = Request.Builder()
            .url(httpUrl)
            .build()
        try {
            mOkHttpClient
                .newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            mBinding.tvTitle.text = getString(R.string.error_get_suggest)
                            showNoResult(R.string.error_get_suggest)
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.body?.string()?.let {
                            handleSuggestData(it)
                        }
                    }
                })
        } catch (e: IOException) {
            showNoResult(R.string.error_get_suggest)
            e.printStackTrace()
        }
    }

    private fun handleSuggestData(data: String) {
        LogUtil.i(TAG, "handleSuggestData: $data")
        val gson = Gson()
        try {
            val listType = object : TypeToken<ArrayList<RepoSubjectSuggest>>() {}.type
            val suggests = gson.fromJson<ArrayList<RepoSubjectSuggest>>(data, listType)
            if (suggests.isNotEmpty()) {
                LogUtil.d(TAG, "handleSuggestData: title=${suggests[0].title}")
                mSubjectId = suggests[0].id
                if (mSubjectId.isNotBlank()) {
                    getInfoBySubjectId(mSubjectId)
                }
            } else {
                showNoResult(R.string.no_result)
            }
        } catch (e: Exception) {
            showNoResult(R.string.no_result)
            e.printStackTrace()
        }
    }

    private fun showLoading() {
        mBinding.btnAdd.visibility = View.GONE
        mBinding.progressBar.visibility = View.VISIBLE
    }

    private fun showNoResult(@StringRes title: Int) {
        runOnUiThread {
            mBinding.progressBar.visibility = View.GONE
            mBinding.btnAdd.visibility = View.GONE
            mBinding.tvTitle.text = getString(title)
            mBinding.tvTitle.visibility = View.VISIBLE
        }
    }

    private fun getInfoBySubjectId(id: String) {
        LogUtil.d(TAG, "getInfoBySubjectId: id=$id")
        lifecycleScope.launch(Dispatchers.Main) {
            val bookInfo = async(Dispatchers.IO) {
                DoubanSubjectParse.getBookInfoBySubjectId(id)
            }
            bookInfo.await()?.let {
                it.lastOpenedTime = System.currentTimeMillis()
                mBookInfo = it
                updateBookInfoUI(it)
                mBinding.btnAdd.visibility = View.VISIBLE
            } ?: showNoResult(R.string.no_result)
        }
    }

    private fun updateBookInfoUI(info: BookInfo) {
        mBinding.progressBar.visibility = View.INVISIBLE
        mBinding.tvTitle.text = info.title
        LogUtil.i(TAG, "updateBookInfoUI: imageUrl: ${info.image}")
        info.image?.let {
            if (it.endsWith("jpg")) {
                val url = it.substring(0, it.lastIndexOf(".")) + ".jpeg"
                mBinding.ivCover.load(url) {
                    crossfade(true)
                }
            } else {
                mBinding.ivCover.load(it) {
                    crossfade(true)
                }
            }

            // subtitle
            if (info.subtitle != null) {
                mBinding.tvSubtitle.visibility = View.VISIBLE
                mBinding.tvSubtitle.text = getString(R.string.dis_subtitle, info.subtitle)
            } else {
                mBinding.tvSubtitle.visibility = View.GONE
            }
            // author
            if (info.author != null) {
                mBinding.tvAuthor.visibility = View.VISIBLE
                mBinding.tvAuthor.text = getString(R.string.dis_author, info.author)
            } else {
                mBinding.tvAuthor.visibility = View.GONE
            }
            // original title
            if (info.originalTitle != null) {
                mBinding.tvOriginalTitle.visibility = View.VISIBLE
                mBinding.tvOriginalTitle.text =
                    getString(R.string.dis_original_title, info.originalTitle)
            } else {
                mBinding.tvOriginalTitle.visibility = View.GONE
            }
            // translator
            if (info.translator != null) {
                mBinding.tvTranslator.visibility = View.VISIBLE
                mBinding.tvTranslator.text = getString(R.string.dis_translator, info.translator)
            } else {
                mBinding.tvTranslator.visibility = View.GONE
            }
            // publish
            if (info.publisher != null) {
                mBinding.tvPublisher.visibility = View.VISIBLE
                mBinding.tvPublisher.text = getString(R.string.dis_publisher, info.publisher)
            } else {
                mBinding.tvPublisher.visibility = View.GONE
            }
            // publish date
            if (info.publishDate != null) {
                mBinding.tvDate.visibility = View.VISIBLE
                mBinding.tvDate.text = getString(R.string.dis_publish_date, info.publishDate)
            } else {
                mBinding.tvDate.visibility = View.GONE
            }
            //pages
            if (info.pages != null) {
                mBinding.tvPages.visibility = View.VISIBLE
                mBinding.tvPages.text = getString(R.string.dis_pages, info.pages)
            } else {
                mBinding.tvPages.visibility = View.GONE
            }
            // price
            if (info.price != null) {
                mBinding.tvPrice.visibility = View.VISIBLE
                mBinding.tvPrice.text = getString(R.string.dis_price, info.price)
            } else {
                mBinding.tvPrice.visibility = View.GONE
            }
            // isbn
            mBinding.tvIsbn.visibility = View.VISIBLE
            mBinding.tvIsbn.text = getString(R.string.dis_isbn, info.isbn)
            // desc
            mBinding.tvDescription.text = info.description
        }
    }

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
    }

    private fun showCloseDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Exit")
            .setMessage("Are you really want to close the app")
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
                finishAffinity()
            }
            .setNegativeButton("Stay", null)
            .setCancelable(true)
            .show()
    }
}