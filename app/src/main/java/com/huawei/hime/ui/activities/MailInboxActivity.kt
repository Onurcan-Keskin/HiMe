package com.huawei.hime.ui.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.helpers.Constants
import com.huawei.hime.ui.interfaces.IMessageInbox
import com.huawei.hime.ui.presenters.MessageInboxPresenter
import com.huawei.hime.util.showLogError
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_mail_inbox.*

class MailInboxActivity : AppCompatActivity(), IMessageInbox.ViewMessageInbox {

    private lateinit var mStorageRef: StorageReference

    private lateinit var mImageBanner: ImageView

    private val messageInboxPresenter: MessageInboxPresenter by lazy {
        MessageInboxPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mail_inbox)
        supportActionBar?.hide()
        messageInboxPresenter.onViewCreate()
    }

    override fun initView() {
        mStorageRef = FirebaseStorage.getInstance().reference

        mImageBanner = findViewById(R.id.mailInbox_header_img)

    }

    override fun populateViewsWithDB() {
        Constants.fUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showLogError(this.javaClass.simpleName,p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                val txt = p0.child("nameSurname").value.toString()
                val image = p0.child("photoUrl").value.toString()

                mailInbox_toolbar.title = txt

                Picasso.get().load(image)
                    .into(mImageBanner)
            }

        })
    }
}
