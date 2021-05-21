package com.huawei.hime.chat

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huawei.hime.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mChatData: View = itemView
//    val mSendRlt: RelativeLayout
//    val mSendName: TextView
//    val mSendItem: TextView

    val mRlt: RelativeLayout
    val mName: TextView
    val mItem: TextView

    fun setName(name: String?) {
        val mUserName = mChatData.findViewById<TextView>(R.id.message_name)
        mUserName.text = name
    }

    fun setTime(time: String) {
        val mTime = mChatData.findViewById<TextView>(R.id.message_timestamp)
        mTime.text = time
    }

    fun setMessage(message: String) {
        val mMessage = mChatData.findViewById<TextView>(R.id.message_item)
        mMessage.text = message
    }

    fun setImage(image: String) {
        val mUserImage: CircleImageView =
            mChatData.findViewById(R.id.message_pic)
        //Picasso.get().load(image).placeholder(R.drawable.wop_new).into(mUserImage);
        if (image != "default") {
            Picasso.get().load(image).placeholder(R.drawable.splachback)
                .into(mUserImage)
        }
    }

    init {
        mRlt = mChatData.findViewById(R.id.relative)
        mName = mChatData.findViewById(R.id.message_name)
        mItem = mChatData.findViewById(R.id.message_item)

//        mSendRlt = mChatData.findViewById(R.id.send_relative)
//        mSendName = mChatData.findViewById(R.id.send_message_name)
//        mSendItem = mChatData.findViewById(R.id.send_message_item)
    }
}