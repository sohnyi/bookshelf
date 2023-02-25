package com.sohnyi.bookshelf.utils

import android.util.Log
import com.sohnyi.bookshelf.BuildConfig

/**
 * 调试日志打印工具
 */
object LogUtil {

    /**
     * 日志级别
     */
    private const val VERBOSE = 1
    private const val DEBUG = 2
    private const val INFO = 3
    private const val WARN = 4
    private const val ERROR = 5
    private const val NOTHING = 6

    /**
     * 设置日志打印级别
     */
    private val level = if (BuildConfig.DEBUG) VERBOSE else NOTHING

    fun v(tag: String?, msg: String) {
        if (VERBOSE >= level) {
            Log.v(tag, msg)
        }
    }

    fun d(tag: String?, msg: String) {
        if (DEBUG >= level) {
            Log.d(tag, msg)
        }
    }

    fun i(tag: String?, msg: String) {
        if (INFO >= level) {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String?, msg: String) {
        if (WARN >= level) {
            Log.w(tag, msg)
        }
    }

    fun e(tag: String?, msg: String, e: Throwable? = null) {
        if (ERROR >= level) {
            Log.e(tag, msg, e)
        }
    }
}