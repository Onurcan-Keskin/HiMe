package com.huawei.hime.ui.presenters

import com.huawei.hime.ui.interfaces.IMessageInbox

class MessageInboxPresenter constructor(private val messageInboxConstructor: IMessageInbox.ViewMessageInbox){
    fun onViewCreate(){
        messageInboxConstructor.initView()
        messageInboxConstructor.populateViewsWithDB()
    }
}