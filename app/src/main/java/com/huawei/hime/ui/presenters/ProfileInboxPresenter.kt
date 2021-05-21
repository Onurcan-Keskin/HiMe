package com.huawei.hime.profile.profileinbox

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.huawei.hime.R

class ProfileInboxPresenter constructor(private val profileInboxContract: ProfileInboxContract) {

    fun onViewsCreate() {
        profileInboxContract.initViews()
        profileInboxContract.initDB()
    }

    fun messagesRecycler(
        query: Query,
        recycler: RecyclerView,
        context: Context,
        receiverID: String,
        receiverName: String,
        receiverImage: String,
        senderName: String,
        senderImage: String
    ) {
        val options = FirebaseRecyclerOptions.Builder<ProfileInboxData>()
            .setQuery(query, ProfileInboxData::class.java).build()
        val adapterFire =
            object : FirebaseRecyclerAdapter<ProfileInboxData, ProfileInboxHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ProfileInboxHolder {
                    val view = LayoutInflater.from(context)
                        .inflate(R.layout.single_inbox_item, parent, false)
                    return ProfileInboxHolder(view)
                }

                override fun onBindViewHolder(
                    holder: ProfileInboxHolder,
                    position: Int,
                    model: ProfileInboxData
                ) {
                    if (model.from == receiverID) {
                        holder.bindReceiver(receiverImage, receiverName, model.message, model.time)
                        holder.bindSender(senderImage, senderName, model.message, model.time)
                    } else {
                        holder.bindSender(senderImage, senderName, model.message, model.time)
                        holder.bindReceiver(receiverImage, receiverName, model.message, model.time)
                    }
                }
            }
        adapterFire.startListening()
        recycler.adapter = adapterFire
    }
}

interface ProfileInboxContract {
    fun initViews()
    fun initDB()
    fun setupRecycler()
}

data class ProfileInboxData(
    val from: String = "",
    val message: String = "",
    val seen: String = "",
    val time: String = "",
    val type: String = ""
)