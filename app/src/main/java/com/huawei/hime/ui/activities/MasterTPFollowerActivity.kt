package com.huawei.hime.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.Query
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.Constants
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.masterfollowers.masterTP.MasterTPContract
import com.huawei.hime.masterfollowers.masterTP.MasterTPPresenter
import com.huawei.hime.util.LocaleHelper
import com.huawei.hime.util.SharedPreferencesManager
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_master_t_p_follower.*

class MasterTPFollowerActivity : AppCompatActivity(), MasterTPContract {

	private lateinit var query : Query
	private lateinit var searchedText : String

	private var tpName : String? = ""

	private val masterTPPresenter : MasterTPPresenter by lazy {
		MasterTPPresenter(this)
	}

	override fun onCreate(savedInstanceState : Bundle?) {
		val sharedPrefs = SharedPreferencesManager(this)
		/* Mode State */
		if (sharedPrefs.loadNightModeState())
			setTheme(R.style.DarkMode)
		else
			setTheme(R.style.LightMode)
		/* Language State */
		if (sharedPrefs.loadLanguage() == "tr")
			LocaleHelper.setLocale(this, "tr")
		else
			LocaleHelper.setLocale(this, "en")
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_master_t_p_follower)
		Slidr.attach(this)
		masterTPPresenter.onViewsCreate()
	}

	override fun initViews() {
		searchedText = follower_search.text.toString()
		tp_recycler.layoutManager = LinearLayoutManager(this)
		tp_recycler.setHasFixedSize(true)


		if (intent.getStringExtra("followers") != null) {
			totalFollowers.text =
				getString(R.string.num_of_followers, intent.getStringExtra("followers"))
		} else {
			totalFollowers.text = getString(R.string.num_of_followers, "0")
		}

		tpName = intent.getStringExtra("tpName")
		val from = intent.getStringExtra("from")

		if (intent.getStringExtra("from") != null) {
			when (intent.getStringExtra("from")) {
				"tag" -> {
					//Bind Tags
					query = FirebaseDBHelper.getTagFollowingNumbers(tpName!!)

					follower_search.addTextChangedListener(object : TextWatcher {
						override fun beforeTextChanged(
							s : CharSequence?,
							start : Int,
							count : Int,
							after : Int
						) {
						}

						override fun onTextChanged(
							s : CharSequence?,
							start : Int,
							before : Int,
							count : Int
						) {
							masterTPPresenter.bindTP(
								this@MasterTPFollowerActivity,
								from!!,
								single_tp_name,
								tpName!!,
								query,
								tp_recycler,
								s.toString(),
								AppUser.getUserId()
							)
						}

						override fun afterTextChanged(s : Editable?) {}
					})
				}
				"places" -> {
					query = FirebaseDBHelper.getPlacesFollowingNumbers(tpName!!)
					follower_search.addTextChangedListener(object : TextWatcher {
						override fun beforeTextChanged(
							s : CharSequence?,
							start : Int,
							count : Int,
							after : Int
						) {
						}

						override fun onTextChanged(
							s : CharSequence?,
							start : Int,
							before : Int,
							count : Int
						) {
							masterTPPresenter.bindTP(
								this@MasterTPFollowerActivity,
								from!!,
								single_tp_name,
								tpName!!,
								query,
								tp_recycler,
								s.toString(),
								AppUser.getUserId()
							)
						}

						override fun afterTextChanged(s : Editable?) {}
					})
				}
			}
		}
	}

	override fun initTagDB(string : String) {

	}

	override fun onStart() {
		super.onStart()
		FirebaseDBHelper.onConnect(Constants.fOnlineDBRef)
	}

	override fun onResume() {
		super.onResume()
		FirebaseDBHelper.onConnect(Constants.fOnlineDBRef)
	}

	override fun onPause() {
		super.onPause()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
	}

	override fun onDestroy() {
		super.onDestroy()
		FirebaseDBHelper.onDisconnect(Constants.fOnlineDBRef)
	}
}