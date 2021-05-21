package com.huawei.hime.ui.presenters

import android.util.Log
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.interfaces.IChat
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatPresenter constructor(private val chatContractor: IChat.ViewChat) {
    fun onViewCreate(){
        chatContractor.initViews()
        chatContractor.populateViewsWithDB()
    }

    fun sendChatMessage(message:String,currentID:String,userID:String){

        if (message != "") {
            val currentUserRef = "Messages/$currentID/$userID"
            val chatUserRef = "Messages/$userID/$currentID"

            val userMessagePush = FirebaseDBHelper.rootRef()
                .child("Messages")
                .child(currentID)
                .child(userID!!).push()

            val pushMessageKey = userMessagePush.key.toString()

            val messageReceiverMap : MutableMap<String, String> = HashMap()
            messageReceiverMap["message"] = message
            messageReceiverMap["seen"] = "false"
            messageReceiverMap["time"] = DateFormat.getDateTimeInstance().format(Date())
            messageReceiverMap["type"] = "text"
            messageReceiverMap["from"] = currentID

            val messageUserMap : MutableMap<String, Any> = HashMap()
            messageUserMap["$currentUserRef/$pushMessageKey"] = messageReceiverMap
            messageUserMap["$chatUserRef/$pushMessageKey"] = messageReceiverMap

            FirebaseDBHelper.rootRef().updateChildren(
                messageUserMap
            ) { databaseError, _ ->
                if (databaseError != null) {
                    Log.d("ChatLog", databaseError.message)
                }
            }
        }
    }
}