package com.huawei.hime.gdpr

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.huawei.hime.ui.activities.InterestsActivity
import com.huawei.hime.R
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.views.expandView
import kotlinx.android.synthetic.main.gdpr_table.*

class GdprActivity : AppCompatActivity(), View.OnClickListener , GdprContract{

    private lateinit var gdprDialog: View
    private lateinit var gdprTable: View
    private lateinit var gdprTableTree: LinearLayout

    private lateinit var privacyCheck: CheckBox
    private lateinit var privacyBtn: Button

    private lateinit var expContainer: ViewGroup

    private val presenter : GdprPresenter by lazy {
        GdprPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPrefs = SharedPreferencesManager(this)
        if (sharedPrefs.loadNightModeState()) {
            setTheme(R.style.DarkMode)
        } else {
            setTheme(R.style.LightMode)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gdpr)
        presenter.onViewCreate()
        supportActionBar?.hide()
    }

    override fun initViews() {
        gdprDialog = findViewById(R.id.gdpr_dialog)
        gdprTable = gdprDialog.findViewById(R.id.gdpr_table_layout)

        expContainer = findViewById(R.id.gdpr_act)

        gdpr_one_layout.setOnClickListener(this)
        gdpr_two_layout.setOnClickListener(this)
        gdpr_three_layout.setOnClickListener(this)
        gdpr_four_layout.setOnClickListener(this)
        gdpr_five_layout.setOnClickListener(this)
        gdpr_six_layout.setOnClickListener(this)
        gdpr_seven_layout.setOnClickListener(this)
        gdpr_eight_layout.setOnClickListener(this)
        gdpr_nine_layout.setOnClickListener(this)
        gdpr_ten_layout.setOnClickListener(this)
        gdpr_eleven_layout.setOnClickListener(this)
        gdpr_twelve_layout.setOnClickListener(this)
        gdpr_thirteen_layout.setOnClickListener(this)
        gdpr_fourteen_layout.setOnClickListener(this)

        gdprTableTree = gdprTable.findViewById(R.id.gdpr_table_tree)

        privacyCheck = gdprDialog.findViewById(R.id.privacy_checkbox)
        privacyBtn = gdprDialog.findViewById(R.id.privacy_btn)

        privacyCheck.setOnClickListener(this)
        privacyBtn.setOnClickListener(this)

        (gdprTableTree as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

    }

    private fun unleashCard(cardView : View , arrowImage : ImageView){
        cardView.expandView()
        if (cardView.visibility == View.GONE) {
            arrowImage.setImageResource(R.drawable.ic_arrow_right_24dp)
        } else {
            arrowImage.setImageResource(R.drawable.ic_arrow_down_24dp)
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.CHANGE_NETWORK_STATE,
                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            11
        )
    }

    @SuppressLint("WrongConstant")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            11 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(
                        expContainer,
                        getText(R.string.prompt_check),
                        Snackbar.LENGTH_SHORT
                    )
                } else {
                    requestPermission()
                }
                return
            }
        }
    }

    private fun openActivity() {
        val intent = Intent(this@GdprActivity, InterestsActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("WrongConstant")
    private fun checkAllow() {
        if (privacyCheck.isChecked) {
            //requestPermission()
            privacyBtn.isEnabled = true
            privacyBtn.isActivated = true
            privacyBtn.isClickable = true
            openActivity()
        } else {
            privacyBtn.isEnabled = false
            privacyBtn.isActivated = false
            privacyBtn.isClickable = false
            Snackbar.make(expContainer,getText(R.string.prompt_check),Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.gdpr_one_layout -> unleashCard(gdpr_card_one,gdpr_one_image)
            R.id.gdpr_two_layout -> unleashCard(gdpr_card_two,gdpr_two_image)
            R.id.gdpr_three_layout -> unleashCard(gdpr_card_three,gdpr_three_image)
            R.id.gdpr_four_layout -> unleashCard(gdpr_card_four,gdpr_four_image)
            R.id.gdpr_five_layout -> unleashCard(gdpr_card_five,gdpr_five_image)
            R.id.gdpr_six_layout -> unleashCard(gdpr_card_six,gdpr_six_image)
            R.id.gdpr_seven_layout -> unleashCard(gdpr_card_seven,gdpr_seven_image)
            R.id.gdpr_eight_layout -> unleashCard(gdpr_card_eight,gdpr_eight_image)
            R.id.gdpr_nine_layout -> unleashCard(gdpr_card_nine,gdpr_nine_image)
            R.id.gdpr_ten_layout -> unleashCard(gdpr_card_ten,gdpr_ten_image)
            R.id.gdpr_eleven_layout -> unleashCard(gdpr_card_eleven,gdpr_eleven_image)
            R.id.gdpr_twelve_layout -> unleashCard(gdpr_card_twelve,gdpr_twelve_image)
            R.id.gdpr_thirteen_layout -> unleashCard(gdpr_card_thirteen,gdpr_thirteen_image)
            R.id.gdpr_fourteen_layout -> unleashCard(gdpr_card_fourteen,gdpr_fourteen_image)

            R.id.privacy_checkbox -> checkAllow()
            R.id.privacy_btn -> openActivity()
        }
    }
}
