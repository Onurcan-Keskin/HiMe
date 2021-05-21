package com.huawei.hime.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.search.tagsearch.TagSearchContract
import com.huawei.hime.search.tagsearch.TagSearchPresenter
import com.huawei.hime.util.NumberConvertor
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.showLogDebug

class TagSearchFragment : Fragment(), TagSearchContract {

    private lateinit var mView: View

    private lateinit var query: Query
    private lateinit var mRecycler: RecyclerView
    private lateinit var tagText: String
    private lateinit var context: FragmentActivity

    private lateinit var mTagDBRef: DatabaseReference
    private lateinit var searchResult: TextView
    private lateinit var inputEditText: TextInputEditText

    private var mUploadArray: MutableList<String> = ArrayList()
    private var mTagArray: MutableList<String> = ArrayList()

    private lateinit var sharedPrefs: SharedPreferencesManager

    private val tagSearchPresenter: TagSearchPresenter by lazy {
        TagSearchPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        context = activity!!
        sharedPrefs = SharedPreferencesManager(context)
        if (sharedPrefs.loadNightModeState()) {
            context.setTheme(R.style.DarkMode)
        } else {
            context.setTheme(R.style.LightMode)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_tag_search, container, false)
        tagSearchPresenter.onViewsCreate()
        return mView
    }

    override fun initViews() {
        inputEditText = mView.findViewById(R.id.ts_text_input)
        searchResult = mView.findViewById(R.id.search_result)
        mRecycler = mView.findViewById(R.id.ts_recycler)
        mRecycler.layoutManager = LinearLayoutManager(context)
        mRecycler.setHasFixedSize(true)

        if (context.intent.getStringExtra("tag") != null) {
            tagText = context.intent.getStringExtra("tag")!!
            inputEditText.setText(tagText)
        } else tagText = ""

        tagText = inputEditText.text.toString()

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (tagText != "") {
                    tagText = inputEditText.text.toString().trim().decapitalize()
                    showLogDebug(mTag, tagText)
                    initDB(s.toString())
                    // initTags(s.toString())
                } else {
                    tagText = inputEditText.text.toString().trim().decapitalize()
                    showLogDebug(mTag, tagText + " " + s.toString())
                    initDB(s.toString())
                    //initTags(s.toString())
                }
            }
        })
        //initDB()
        //initTags()
    }

    fun initTags(string: String) {
//        val hashDB = DbUtils.rootRef().child("Tag").orderByKey().equalTo(tagText)
//        hashDB.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                for (postSnap in p0.children) {
//                    val key = postSnap.key!!
//                    mTagArray.add(key)
//                    showLogDebug(mTag, "Tag Keys - for $mTagArray")
//                }
//                var i = 0
//                while (i <= (mTagArray.size) - 1) {
//                    showLogDebug(mTag, "Tag Keys - while $mTagArray")
//                    tagSearchPresenter.setupHashtagRecycler(
//                        hashDB,
//                        mTagArray.size,
//                        mTagArray,
//                        mRecycler,
//                        context,
//                        string
//                    )
//                    i++
//                }
//            }
//        })
    }

    override fun initDB(string: String) {
        mTagDBRef = FirebaseDBHelper.rootRef().child("Tag")
        mTagDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (postSnap in p0.children) {
                        val key = postSnap.key!!
                        mUploadArray.add(key)
                    }
                    var i = 0
                    while (i <= (mUploadArray.size) - 1) {
                        if (mUploadArray.size == 0) {
                            query = FirebaseDBHelper.getShareableUploads()
                        } else {
                            val hashDB = FirebaseDBHelper.getTagReference(mUploadArray[i])
                            hashDB.addValueEventListener(object :ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {
                                }
                                override fun onDataChange(p0: DataSnapshot) {
                                    val pTotal = NumberConvertor.prettyCount(p0.childrenCount)
                                    tagSearchPresenter.setupHashtagRecycler(
                                        mTagDBRef,
                                        mUploadArray.size,
                                        mUploadArray,
                                        mRecycler,
                                        context,
                                        string,
                                        pTotal
                                    )
                                }
                            })

                            //RecyclerTypes.setupRecyclerHybrid(query, context, mRecycler)
                            i++
                        }
                    }
                }
            }
        })
    }

    override fun setPrefs() {
        if (sharedPrefs.loadNightModeState())
            inputEditText.background = context.getDrawable(R.drawable.dark_mask_bottom)
        else
            inputEditText.background = context.getDrawable(R.drawable.search_mask_bottom_light)
    }

    companion object {
        private const val mTag = "TagSearchFragment"
    }
}
