package com.huawei.hime.search.placesearch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.SinglePlacesActivity
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.UploadTravelDataClass
import com.huawei.hime.util.showLogDebug

class PlacesSearchPresenter constructor(private val placesSearchContract: PlacesSearchContract) {

    fun onViewsCreated() {
        placesSearchContract.initViews()
    }

    fun setPrefs(
        context: Context,
        sharedPreferencesManager: SharedPreferencesManager,
        textView: TextInputEditText
    ) {
        if (sharedPreferencesManager.loadNightModeState())
            textView.background = context.getDrawable(R.drawable.dark_mask_bottom)
        else
            textView.background = context.getDrawable(R.drawable.search_mask_bottom_light)
    }

    fun setRecycler(
        context: Context,
        query: Query,
        searchedText: String,
        mRecycler: RecyclerView
    ) {
        val options = FirebaseRecyclerOptions.Builder<UploadTravelDataClass>()
            .setQuery(query, UploadTravelDataClass::class.java).build()

        val adapterFire = object :
            FirebaseRecyclerAdapter<UploadTravelDataClass, PlacesSearchViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): PlacesSearchViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.single_country_layout, parent, false)
                return PlacesSearchViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: PlacesSearchViewHolder,
                position: Int,
                model: UploadTravelDataClass
            ) {
                val lisResUid = getRef(position).key
                var pTotal: String
                val beautifyAddress = (model.upload_travel_address).decapitalize().trim()
                showLogDebug(mTag, "Before $beautifyAddress | $searchedText")
                if (model.upload_travel_address.contains(searchedText)) {
                    showLogDebug(
                        mTag,
                        "After $beautifyAddress | ${searchedText.decapitalize().trim()}"
                    )
                    val db = FirebaseDBHelper.getShareableUploads().orderByChild("upload_travel_country")
                        .equalTo(model.upload_travel_country)
                    db.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.hasChildren()) {
                                pTotal = NumberConvertor.prettyCount(p0.childrenCount)
                                holder.bindView(model.upload_travel_country, pTotal)
                                holder.parent.setOnClickListener {
                                    context.startActivity(
                                        Intent(
                                            context.applicationContext,
                                            SinglePlacesActivity::class.java
                                        )
                                            .putExtra("country", model.upload_travel_country)
                                            .putExtra("total", pTotal)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                }
                            }
                        }
                    })

                } else {
                    holder.itemView.visibility = View.GONE
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                }
            }
        }
        adapterFire.startListening()
        mRecycler.adapter = adapterFire
    }

    companion object {
        private const val mTag = "PlacesSearchPresenter"
    }
}

interface PlacesSearchContract {
    fun initViews()
    fun initDB(searchedText: String)
}