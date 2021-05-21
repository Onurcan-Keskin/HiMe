package com.huawei.hime.search.placesearch

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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.huawei.hime.R
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.util.SharedPreferencesManager

class PlacesSearchFragment : Fragment(), PlacesSearchContract {

    private lateinit var mView: View
    private lateinit var context: FragmentActivity

    private lateinit var inputEditText: TextInputEditText

    private lateinit var mText: String
    private lateinit var mTotal: String
    private lateinit var mCountryDB: DatabaseReference
    private lateinit var mCountryQuery: Query

    private lateinit var mRecyclerView: RecyclerView

    private lateinit var sharedPrefs: SharedPreferencesManager

    private val placesSearchPresenter: PlacesSearchPresenter by lazy {
        PlacesSearchPresenter(this)
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
        mView = inflater.inflate(R.layout.fragment_places_search, container, false)
        placesSearchPresenter.onViewsCreated()
        //placesSearchPresenter.setPrefs(context, sharedPrefs, text)
        return mView
    }

    override fun initViews() {
        context = activity!!
        inputEditText = mView.findViewById(R.id.ps_text_input)
        mRecyclerView = mView.findViewById(R.id.ps_recycler)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.setHasFixedSize(true)
        mText = inputEditText.text.toString()

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                initDB(s.toString())
            }
        })
    }

    override fun initDB(string: String) {
        mCountryDB = FirebaseDBHelper.getShareableUploads()
        mCountryQuery = mCountryDB.orderByChild("typeGroup").equalTo("0")
        placesSearchPresenter.setRecycler(context, mCountryQuery, string, mRecyclerView)


    }

    companion object {

    }
}
