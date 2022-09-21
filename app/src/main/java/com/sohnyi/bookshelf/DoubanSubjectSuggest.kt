package com.sohnyi.bookshelf

import java.util.ArrayList

object DoubanSubjectSuggest {
    /*fun getSubjectSuggests(@NotNull text: String?) {
        val bookInfoList: MutableList<BookInfo?> = ArrayList()
        val doubanService: DoubanService =
            RetrofitClient.getInstance().create(DoubanService::class.java)
        val call: Call<List<RepoSubjectSuggest>> = doubanService.getSuggest(text)
        call.enqueue(object : Callback<List<RepoSubjectSuggest?>?>() {
            fun onResponse(
                call: Call<List<RepoSubjectSuggest?>?>?,
                response: Response<List<RepoSubjectSuggest?>?>
            ) {
                println("onResponse() thread name: " + Thread.currentThread().name)
                if (response.isSuccessful()) {
                    val suggestList: List<RepoSubjectSuggest> = response.body()
                    if (!suggestList.isEmpty()) {
                        for (suggest in suggestList) {
                            bookInfoList.add(getBookInfo(suggest.getId()))
                        }
                    } else {
                        println("No Data")
                    }
                } else {
                    System.out.println(
                        "response: code=" + response.code()
                            .toString() + ", msg=" + response.message()
                    )
                }
            }

            fun onFailure(call: Call<List<RepoSubjectSuggest?>?>, throwable: Throwable) {
                println("onFailure() thread name: " + Thread.currentThread().name)
                call.cancel()
                throwable.printStackTrace()
            }
        })
    }

    private fun getBookInfo(@NotNull id: String): BookInfo? {
        val parse = DoubanSubjectParse()
        return parse.getBookInfoBySubjectId(id)
    }*/
}