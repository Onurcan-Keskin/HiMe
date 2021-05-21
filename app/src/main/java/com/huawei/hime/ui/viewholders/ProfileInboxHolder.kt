package com.huawei.hime.profile.profileinbox

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huawei.hime.R
import de.hdodenhof.circleimageview.CircleImageView

class ProfileInboxHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val parent: View? = itemView

    /* Sender Holder - Right - UserID */
    var senderRlt: RelativeLayout? = null
    private var senderCircle: CircleImageView? = null
    private var senderName: TextView? = null
    private var senderMessage: TextView? = null
    private var senderTime: TextView? = null

    /* Receiver Holder - Left - CurrentID */
    var receiveRlt: RelativeLayout? = null
    private var receiverCircle: CircleImageView? = null
    private var receiverName: TextView? = null
    private var receiverMessage: TextView? = null
    private var receiverTime: TextView? = null

    fun bindSender(
        messageCircle: String,
        messageName: String,
        messageItem: String,
        messageTime: String
    ) {
        Glide.with(parent!!.context.applicationContext).load(messageCircle).into(senderCircle!!)
        senderName!!.text = messageName
        senderMessage!!.text = messageItem
        senderTime!!.text = messageTime
        //receiveRlt!!.visibility = View.GONE
    }

    fun bindReceiver(
        messageCircle: String,
        messageName: String,
        messageItem: String,
        messageTime: String
    ) {
        Glide.with(parent!!.context.applicationContext).load(messageCircle).into(receiverCircle!!)
        receiverName!!.text = messageName
        receiverMessage!!.text = messageItem
        receiverTime!!.text = messageTime
       // senderRlt!!.visibility = View.GONE
    }

    init {
        /* Sender Holder - Right - UserID */
        senderRlt = parent!!.findViewById(R.id.relative)
        senderCircle = parent.findViewById(R.id.message_pic)
        senderName = parent.findViewById(R.id.message_name)
        senderMessage = parent.findViewById(R.id.message_item)
        senderTime = parent.findViewById(R.id.message_timestamp)


        /* Receiver Holder - Left - CurrentID */
        receiveRlt = parent.findViewById(R.id.relative_receive)
        receiverCircle = parent.findViewById(R.id.message_pic_receive)
        receiverName = parent.findViewById(R.id.message_name_receive)
        receiverMessage = parent.findViewById(R.id.message_item_receive)
        receiverTime = parent.findViewById(R.id.message_timestamp_receive)
    }
}