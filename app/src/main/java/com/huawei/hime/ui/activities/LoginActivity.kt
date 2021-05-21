package com.huawei.hime.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.facebook.FacebookSdk
import com.facebook.login.widget.LoginButton
import com.google.android.material.textfield.TextInputEditText
import com.huawei.hime.R
import com.huawei.hime.gdpr.GdprActivity
import com.huawei.hime.util.LocaleHelper
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.views.expandView
import kotlinx.android.synthetic.main.activity_login.*
import login.HmsGmsLoginHelper
import main.MainActivity
import java.util.*

class LoginActivity : AppCompatActivity(), LoginContract {

	private lateinit var btnEmail : Button
	private lateinit var btnFB : LoginButton
	private lateinit var loginConstraintLyt : ConstraintLayout

	private lateinit var btnG : View

	private lateinit var edMail : TextInputEditText
	private lateinit var edPassword : TextInputEditText

	private var currentlang = "en"
	private lateinit var mLocale : Locale

	private val hmsGmsLoginHelper : LoginHelper by lazy {
		HmsGmsLoginHelper(this)
	}

	private val presenter : LoginPresenter by lazy { LoginPresenter(this, hmsGmsLoginHelper) }

	//--------------------------------------------------
	// Activity - Lifetime
	//--------------------------------------------------
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
		supportActionBar?.hide()
		setContentView(R.layout.activity_login)
		presenter.onViewCreate()

		if (sharedPrefs.loadNightModeState()) {
			dayNight.isChecked = true
			loginConstraintLyt.background =
				ContextCompat.getDrawable(this, R.drawable.ic_back_bubble_dark)
		} else {
			loginConstraintLyt.background =
				ContextCompat.getDrawable(this, R.drawable.background_login)
		}
		dayNight.setOnCheckedChangeListener { _, isChecked ->
			if (isChecked) {
				sharedPrefs.setNightModeState(true)
				recreate()
			} else {
				sharedPrefs.setNightModeState(false)
				recreate()
			}
		}

		login_settings_visibility_controller.setOnClickListener {
			login_settings_card.expandView()
			if (login_settings_card.visibility == View.GONE) {
				login_settings_arrow.setImageResource(R.drawable.ic_arrow_downward)
			} else {
				login_settings_arrow.setImageResource(R.drawable.ic_arrow_upward)
			}
		}

		log_tr.setOnClickListener {
			currentlang = "tr"
			LocaleHelper.setLocale(this, currentlang)
			restartApp()
		}

		log_en.setOnClickListener {
			currentlang = "en"
			LocaleHelper.setLocale(this, currentlang)
			restartApp()
		}
	}

	//--------------------------------------------------
	// Activity - Custom
	//--------------------------------------------------

	override fun initViews() {
		FacebookSdk.sdkInitialize(applicationContext)
		btnEmail = findViewById(R.id.login_email_signIn_btn)
		btnFB = findViewById(R.id.login_fb_btn)
		loginConstraintLyt = findViewById(R.id.login_constraint_layout)
		edMail = findViewById(R.id.login_email)
		edPassword = findViewById(R.id.login_password)
		btnG = findViewById(R.id.login_btn)

		btnEmail.setOnClickListener {
			presenter.onEmailBtnClick()
		}

		btnFB.setOnClickListener {
			presenter.onFacebookSignInClick()
		}

		btnG.setOnClickListener {
			presenter.onSignInGHClick()
		}
		//fbLoginBtn = findViewById(R.id.login_fb_btn)
	}

	override fun redirectToGdpr() {
		startActivity(Intent(this, GdprActivity::class.java))
	}

	override fun redirectToSignIn(signInIntent : Intent, requestCode : Int) {
		startActivityForResult(signInIntent, requestCode)
	}

	override fun redirectToMain() {
		startActivity(
            MainActivity.getIntent(this)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
		finish()
	}

	override fun restartApp() {
		startActivity(Intent(applicationContext, LoginActivity::class.java))
		finish()
	}

	override fun showUpdateMessage(message : String) {

	}

	override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		hmsGmsLoginHelper.onDataReceived(requestCode, resultCode, data)
		//hmsGmsLoginHelper
	}

}
