package com.sohnyi.bookshelf

import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.client.android.BuildConfig
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.sohnyi.bookshelf.databinding.ActivityMainBinding
import com.sohnyi.bookshelf.model.RepoSubjectSuggest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            getUrlBodyByCode(result.contents)
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
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


    private var subjectId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener { scanBarCode() }
    }

    private fun scanBarCode() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
            setCameraId(0)
            setPrompt("scan")
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
        }

        barcodeLauncher.launch(options)
    }

    private fun getUrlBodyByCode(code: String) {
        val httpUrl = HttpUrl.Builder()
            .scheme(Uri.parse(BASE_URL_DOUBAN_BOOK).scheme ?: "")
            .host(Uri.parse(BASE_URL_DOUBAN_BOOK).host ?: "")
            .addEncodedPathSegments(PATH_SUBJECT_SUGGEST)
            .addQueryParameter("q", code)
            .build()

        Log.i(TAG, "getUrlBody: ${httpUrl.toUrl()}")
        val request = Request.Builder()
            .url(httpUrl)
            .build()
        try {
            okHttpClient
                .newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            binding.tvTitle.text = getString(R.string.error_get_suggest)
                            binding.progressBar.visibility = View.INVISIBLE
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.body?.string()?.let {
                            handleSuggestData(it)
                        }
                    }
                })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun handleSuggestData(data: String) {
        Log.i(TAG, "handleSuggestData: $data")
        val gson = Gson()
        try {
            val listType = object : TypeToken<ArrayList<RepoSubjectSuggest>>() {}.type
            val suggests = gson.fromJson<ArrayList<RepoSubjectSuggest>>(data, listType)
            Log.d(TAG, "handleSuggestData: title=${suggests[0].title}")
            subjectId = suggests[0].id
            if (subjectId.isNotBlank()) {
                getInfoBySubjectId(subjectId)
            }
        } catch (e: Exception) {
        }
    }

    private fun getInfoBySubjectId(id: String) {
        Log.d(TAG, "getInfoBySubjectId: id=$id")
        lifecycleScope.launch(Dispatchers.Main) {
            val bookInfo = withContext(Dispatchers.IO) {
                DoubanSubjectParse.getBookInfoBySubjectId(id)
            }
            bookInfo?.let {
                updateBookInfoUI(it)
            }
        }
    }

    private fun updateBookInfoUI(info: DoubanBookInfo) {
        binding.progressBar.visibility = View.INVISIBLE
        binding.tvTitle.text = info.title
        Log.i(TAG, "updateBookInfoUI: imageUrl: ${info.image}")
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

            // subtitle
            if (info.subtitle != null) {
                binding.tvSubtitle.visibility = View.VISIBLE
                binding.tvSubtitle.text = getString(R.string.dis_subtitle, info.subtitle)
            } else {
                binding.tvSubtitle.visibility = View.GONE
            }
            // author
            if (info.author != null) {
                binding.tvAuthor.visibility = View.VISIBLE
                binding.tvAuthor.text = getString(R.string.dis_author, info.author)
            } else {
                binding.tvAuthor.visibility = View.GONE
            }
            // original title
            if (info.originalTitle != null) {
                binding.tvOriginalTitle.visibility = View.VISIBLE
                binding.tvOriginalTitle.text =
                    getString(R.string.dis_original_title, info.originalTitle)
            } else {
                binding.tvOriginalTitle.visibility = View.GONE
            }
            // translator
            if (info.translator != null) {
                binding.tvTranslator.visibility = View.VISIBLE
                binding.tvTranslator.text = getString(R.string.dis_translator, info.translator)
            } else {
                binding.tvTranslator.visibility = View.GONE
            }
            // publish
            if (info.publisher != null) {
                binding.tvPublisher.visibility = View.VISIBLE
                binding.tvPublisher.text = getString(R.string.dis_publisher, info.publisher)
            } else {
                binding.tvPublisher.visibility = View.GONE
            }
            // publish date
            if (info.publishDate != null) {
                binding.tvDate.visibility = View.VISIBLE
                binding.tvDate.text = getString(R.string.dis_publish_date, info.publishDate)
            } else {
                binding.tvDate.visibility = View.GONE
            }
            //pages
            if (info.pages != null) {
                binding.tvPages.visibility = View.VISIBLE
                binding.tvPages.text = getString(R.string.dis_pages, info.pages)
            } else {
                binding.tvPages.visibility = View.GONE
            }
            // price
            if (info.price != null) {
                binding.tvPrice.visibility = View.VISIBLE
                binding.tvPrice.text = getString(R.string.dis_price, info.price)
            } else {
                binding.tvPrice.visibility = View.GONE
            }
            // isbn
            binding.tvIsbn.visibility = View.VISIBLE
            binding.tvIsbn.text = getString(R.string.dis_isbn, info.id)
            // desc
            binding.tvDescription.text = info.description
        }
    }
}