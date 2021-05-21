package com.huawei.hime.profile

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.database.DatabaseReference
import com.huawei.hime.R
import com.huawei.hime.util.showLogDebug

class ProfilePresenter constructor(private val profileContract: ProfileContract) {

    var privacyByte: MutableList<String> = ArrayList()


    fun onViewsCreate() {
        profileContract.initViews()
        profileContract.loadData()
        profileContract.initDB()
    }

    fun checkedPrivacy(
        sMessages: SwitchCompat,
        sMemory: SwitchCompat,
        sPost: SwitchCompat,
        sFollowers: SwitchCompat,
        privacy: String,
        databaseReference: DatabaseReference,
        image: ImageView,
        context: Context
    ) {
        privacyByte = privacy.split("") as MutableList<String>  // [, 1, 1, 1, 1, ]
        showLogDebug("Privacy", privacyByte.toString() + " " + setPr(privacyByte))
        sMessages.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                privacyByte[1] = "0"
                showLogDebug("checkedPrivacy", privacyByte.toString())
                //setPr(privacyByte)
                setPrivacyLight(setPr(privacyByte), image)
                sMessages.text = context.getString(R.string.hide_message)
                databaseReference.child("privacy").setValue(setPr(privacyByte))
            } else {
                privacyByte[1] = "1"
                showLogDebug("checkedPrivacy", privacyByte.toString())
                setPrivacyLight(setPr(privacyByte), image)
                sMessages.text = context.getString(R.string.show_message)
                databaseReference.child("privacy").setValue(setPr(privacyByte))
            }
        }
        sMemory.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                privacyByte[2] = "0"
                showLogDebug("checkedPrivacy", privacyByte.toString())
                setPrivacyLight(setPr(privacyByte), image)
                sMemory.text = context.getString(R.string.hide_memory)
                databaseReference.child("privacy").setValue(setPr(privacyByte))
            } else {
                privacyByte[2] = "1"
                showLogDebug("checkedPrivacy", privacyByte.toString())
                setPrivacyLight(setPr(privacyByte), image)
                sMemory.text = context.getString(R.string.show_memory)
                databaseReference.child("privacy").setValue(setPr(privacyByte))
            }
        }
        sFollowers.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                privacyByte[3] = "0"
                showLogDebug("checkedPrivacy", privacyByte.toString())
                setPrivacyLight(setPr(privacyByte), image)
                sFollowers.text = context.getString(R.string.hide_followers)
                databaseReference.child("privacy").setValue(setPr(privacyByte))
            } else {
                privacyByte[3] = "1"
                showLogDebug("checkedPrivacy", privacyByte.toString())
                setPrivacyLight(setPr(privacyByte), image)
                sFollowers.text = context.getString(R.string.show_followers)
                databaseReference.child("privacy").setValue(setPr(privacyByte))
            }
        }
        sPost.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                privacyByte[4] = "0"
                showLogDebug("checkedPrivacy", privacyByte.toString())
                setPrivacyLight(setPr(privacyByte), image)
                sPost.text = context.getString(R.string.hide_shares)
                databaseReference.child("privacy").setValue(setPr(privacyByte))
            } else {
                privacyByte[4] = "1"
                showLogDebug("checkedPrivacy", privacyByte.toString())
                setPrivacyLight(setPr(privacyByte), image)
                sPost.text = context.getString(R.string.show_shares)
                databaseReference.child("privacy").setValue(setPr(privacyByte))
            }
        }

        showLogDebug("checkedPrivacy", privacyByte.toString())
    }

    fun setPrivacyLight(
        mPrivacy: String, image:
        ImageView
    ) {
        showLogDebug("setPrivacyLight", mPrivacy)
        when (mPrivacy) {
            //16 possibilities -> 2^4 [0,0,0,0,1]
            //                                 | dummy
            "0000" -> {
                image.setImageResource(R.drawable.secret_type_0000_l)
            }
            "0001" -> {
                image.setImageResource(R.drawable.secret_type_0001_l)
            }
            "0010" -> {
                image.setImageResource(R.drawable.secret_type_0010_l)
            }
            "0011" -> {
                image.setImageResource(R.drawable.secret_type_0011_l)
            }
            "0100" -> {
                image.setImageResource(R.drawable.secret_type_0100_l)
            }
            "0101" -> {
                image.setImageResource(R.drawable.secret_type_0101_l)
            }
            "0110" -> {
                image.setImageResource(R.drawable.secret_type_0110_l)
            }
            "0111" -> {
                image.setImageResource(R.drawable.secret_type_0111_l)
            }
            "1000" -> {
                image.setImageResource(R.drawable.secret_type_1000_l)
            }
            "1001" -> {
                image.setImageResource(R.drawable.secret_type_1001_l)
            }
            "1010" -> {
                image.setImageResource(R.drawable.secret_type_1010_l)
            }
            "1011" -> {
                image.setImageResource(R.drawable.secret_type_1011_l)
            }
            "1100" -> {
                image.setImageResource(R.drawable.secret_type_1100_l)
            }
            "1101" -> {
                image.setImageResource(R.drawable.secret_type_1101_l)
            }
            "1110" -> {
                image.setImageResource(R.drawable.secret_type_1110_l)
            }
            "1111" -> {
                image.setImageResource(R.drawable.secret_type_1111_l)
            }
        }
    }
}

private fun setPr(privacyByte: MutableList<String>): String {
    return privacyByte.toString().trim()
        .replace("[", "")
        .replace(",", "")
        .replace(" ", "")
        .replace("]", "")
}


interface ProfileContract {
    fun initViews()
    fun initDB()
    fun loadData()
    fun setupSpinner()
    fun setUpSheets()
    fun restartApp()
    fun menuGetSettingsPopup()
    fun getLightPopup()
    fun getDarkPopup()
    fun tryToGetAppVersion()
}