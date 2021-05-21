package com.huawei.hime.search.usersearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.showLogDebug

class UserSearchFragment : Fragment(), UserSearchContract {

    private lateinit var mView: View
    private lateinit var context: FragmentActivity

    private val currentID = AppUser.getUserId()

    private lateinit var inputEditText: TextInputEditText
    private lateinit var textText: String

    private lateinit var sharedPrefs: SharedPreferencesManager

    private lateinit var mUserDBRef: DatabaseReference
    private lateinit var query: Query
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var nameSurname: String

    private val userSearchPresenter: UserSearchPresenter by lazy {
        UserSearchPresenter(this)
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
        mView = inflater.inflate(R.layout.fragment_user_search, container, false)
        userSearchPresenter.onViewsCreate()
        //userSearchPresenter.setPrefs(context, sharedPrefs, inputEditText)
        return mView
    }

    override fun initViews() {
        inputEditText = mView.findViewById(R.id.us_text_input)
        mRecyclerView = mView.findViewById(R.id.us_recycler)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.setHasFixedSize(true)
        textText = inputEditText.text.toString()

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showLogDebug(mTag, "onTextChanged " + s.toString() + " $textText")
                initDB(s.toString())
            }
        })
    }

    override fun initDB(string: String) {
        mUserDBRef = FirebaseDBHelper.rootRef().child("UserInfo")
        val mutableList: MutableList<String> = ArrayList()
        mUserDBRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    for (postSnap in snapshot.children) {
                        val key = postSnap.key!!
                        mutableList.add(key)
                    }
                    var i = 0
                    while (i < mutableList.size) {
                        val dbRef = FirebaseDBHelper.getUserInfo(mutableList[i])
                        dbRef.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                nameSurname = dataSnapshot.child("nameSurname").value.toString()
                                showLogDebug(mTag, "Name $nameSurname | DB $dbRef")
                                if (nameSurname.contains(string)) {
                                    userSearchPresenter.setupUserRecycler(
                                        context,
                                        mUserDBRef,
                                        currentID,
                                        mRecyclerView,
                                        string
                                    )
                                }
                            }
                        })
                        i++
                    }
                }
            }
        })

//        userSearchPresenter.setupUserRecycler(
//            context,
//            mUserDBRef,
//            currentID,
//            mRecyclerView,
//            string
//        )
    }

    companion object {
        private const val mTag = "UserSearch"
    }
}
