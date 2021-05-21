package com.huawei.hime.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.models.ChatType


class ChatAdapter(private val mMessagesList: ArrayList<ChatType>) :
    RecyclerView.Adapter<ChatViewHolder>() {

    private lateinit var currentUID: String
    private lateinit var fromUID: String

    private var img: String = ""
    private var name: String = ""

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        currentUID = AppUser.getUserId()
        val c = mMessagesList[position]
        Log.d("tag", c.from + " " + c.message + " " + c.time)
        fromUID = c.from
        Log.d("From", c.from)
        //val orange: Int = Color.rgb(230, 81, 0)

        FirebaseDBHelper.getUserInfo(fromUID).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                name = p0.child("nameSurname").value.toString()
                img = p0.child("photoUrl").value.toString()

                if (fromUID == currentUID) {
                    holder.mRlt.visibility = View.VISIBLE
//                    holder.mSendName.text = name
//                    holder.mSendItem.text = c.message
                    holder.setName(name)
                    holder.setMessage(c.message)
                    holder.setImage(img)
                    holder.setTime(c.time)
                } else {
                    holder.mRlt.visibility = View.VISIBLE
//                    holder.mRecName.text = name
//                    holder.mRecItem.text = c.message
                    holder.setName(name)
                    holder.setMessage(c.message)
                    holder.setImage(img)
                    holder.setTime(c.time)
                }
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        currentUID = AppUser.getUserId()
        return if (mMessagesList[position].from == currentUID){
            messageLEFT
        } else {
            messageRIGTH
        }
    }

    @NonNull
    override fun onCreateViewHolder(
        @NonNull parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        return if (viewType == messageRIGTH){
            val v: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.single_message_right, parent, false)
            ChatViewHolder(v)
        } else {
            val v: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.single_message_left, parent, false)
            ChatViewHolder(v)
        }
    }

    override fun getItemCount(): Int {
        return mMessagesList.size
    }

    companion object {
        private const val messageRIGTH = 0
        private const val messageLEFT = 1
    }

}