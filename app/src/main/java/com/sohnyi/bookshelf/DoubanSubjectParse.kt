package com.sohnyi.bookshelf


import androidx.annotation.Nullable
import com.sohnyi.bookshelf.entry.BookInfo
import org.jsoup.Jsoup
import org.jsoup.internal.StringUtil
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeTraversor
import java.io.IOException
import java.lang.StringBuilder
import java.util.HashMap

class DoubanSubjectParse {


    companion object {
        private const val URL_DOUBAN_BOOK_SUBJECT = "https://book.douban.com/subject/"
        private const val META_ATTRIBUTE_KEY = "property"
        private const val OG_IMAGE = "og:image"
        private const val OG_TYPE = "og:type"
        private const val OG_SITE_NAME = "og:site_name"
        private const val OG_ISBN = "book:isbn"
        private const val OG_TITLE = "og:title"
        private const val OG_AUTHOR = "book:author"
        private const val OG_URL = "og:url"
        private const val OG_DESC = "og:description"

        fun getBookInfoBySubjectId(id: String): BookInfo? {
            var bookInfo: BookInfo? = null
            val url = URL_DOUBAN_BOOK_SUBJECT + id
            try {
                val doc: Document = Jsoup.connect(url).get()
                bookInfo = BookInfo(id)
                val metaMap = getMetaFormDocument(doc)
                val infoMap = getInfoFormDocument(doc)
                bookInfo.title = metaMap[OG_TITLE]
                bookInfo.url = metaMap[OG_URL]
                bookInfo.description = metaMap[OG_DESC]
                bookInfo.image = metaMap[OG_IMAGE]
                bookInfo.author = infoMap["作者"]
                bookInfo.publisher = infoMap["出版社"]
                bookInfo.price = infoMap["定价"]
                bookInfo.pages = infoMap["页数"]
                bookInfo.publishDate = infoMap["出版年"]
                bookInfo.translator = infoMap["译者"]
                bookInfo.subtitle = infoMap["副标题"]
                bookInfo.format = infoMap["装帧"]
                bookInfo.originalTitle = infoMap["原作题"]

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return bookInfo
        }

        @Nullable
        private fun getMetaFormDocument(doc: Document): HashMap<String, String> {
            val map = HashMap<String, String>()
            val metas = doc.getElementsByTag("meta")
            for (meta in metas) {
                if (meta.hasAttr("property")) {
                    map[meta.attr("property")] = meta.attr("content")
                }
            }
            return map
        }

        @Nullable
        private fun getInfoFormDocument(doc: Document): HashMap<String, String> {
            // 获取info相关信息
            val info = doc.selectFirst("div#info")
            val accum: StringBuilder = StringUtil.borrowBuilder()
            info?.let {
                NodeTraversor.traverse({ node: Node, _: Int ->
                    if (node is TextNode) {
                        val textNode: TextNode = node
                        accum.append(textNode.wholeText.trim { it <= ' ' })
                    } else if (node is Element) {
                        if (accum.isNotEmpty() &&
                            (node.isBlock || node.tag().normalName() == "br" &&
                                    !StringUtils.lastCharIs(
                                        accum, ','
                                    ))
                        ) {
                            accum.append(',')
                        }
                    }
                }, info)
            }
            val infoText: String = StringUtils.removeCharEndWith(
                StringUtil.releaseBuilder(accum).trim { it <= ' ' },
                ","
            )

            // 包装数据
            val infoArray = infoText.split(",").toTypedArray()
            val map = HashMap<String, String>()
            for (i in infoArray) {
                val kv = i.split(":").toTypedArray()
                map[kv[0].trim { it <= ' ' }] = kv[1].trim { it <= ' ' }
            }
            return map
        }
    }
}