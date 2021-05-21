package com.huawei.hime.videorecord

class RecordVideoPresenter constructor(private val recordVideoContractor: RecordVideoContractor) {

    fun onViewsCreate() {
        recordVideoContractor.initDB()
    }
}

interface RecordVideoContractor {
    fun initDB()
}

data class Upload(
    val posterID: String = "",
    val postUrl: String = "",
    val postName: String = "",
    val postType: String = "",
    val postDate: String = "",
    val postExp: String = ""
)