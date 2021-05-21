package com.huawei.hime.ui.fragments

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.huawei.hime.R
import com.huawei.hime.databinding.FragmentCaptionBinding
import com.huawei.hime.ui.presenters.CaptionContract
import com.huawei.hime.models.CaptionInterestType
import com.huawei.hime.profile.profileevent.caption.CaptionInterestsCustomAdapter
import com.huawei.hime.ui.presenters.CaptionPresenter
import com.huawei.hime.ui.mvp.BaseFragmentExtend
import com.huawei.hime.ui.mvp.HasBackButton
import com.huawei.hime.ui.mvp.HasToolbar
import com.huawei.hime.util.SharedPreferencesManager
import com.huawei.hime.util.showSnackbar

class CaptionFragment : BaseFragmentExtend(R.layout.fragment_caption), HasToolbar, HasBackButton,
	CaptionContract {

	override val toolbar : Toolbar?
		get() = binding.captionToolbar

	override val titleRes : Int? = null

	private var interestsCheck : String = ""

	private lateinit var context : FragmentActivity
	private lateinit var sharedPrefs : SharedPreferencesManager
	private lateinit var binding : FragmentCaptionBinding

	lateinit var captionDataPass : CaptionOnDataPass

	private var interestsList : ArrayList<CaptionInterestType> = ArrayList()
	private var adapter : CaptionInterestsCustomAdapter? = null

	private val captionPresenter : CaptionPresenter by lazy {
		CaptionPresenter(this)
	}

	private var interests = intArrayOf(
		R.drawable.music, R.drawable.theatre, R.drawable.cinema, R.drawable.game,
		R.drawable.travel, R.drawable.sports, R.drawable.entertainment, R.drawable.education
	)

	override fun onCreate(savedInstanceState : Bundle?) {
		context = activity!!
		sharedPrefs = SharedPreferencesManager(context)
		super.onCreate(savedInstanceState)
	}

	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)
		binding = FragmentCaptionBinding.bind(view)
		captionPresenter.onViewsCreate()

		binding.captionSaveButton.setOnClickListener {
			captionPresenter.onSave(adapter!!.imgList)
		}
	}

	override fun initViews() {
		populateGrid()

		adapter = CaptionInterestsCustomAdapter(interestsList, context)
		binding.captionInterestGrd?.adapter = adapter
	}

	override fun populateGrid() {
		val inter1 = CaptionInterestType()
		inter1.imgName = "Music"
		inter1.imgIcon = interests[0]
		interestsList.add(inter1)

		val inter2 = CaptionInterestType()
		inter2.imgName = "Theatre"
		inter2.imgIcon = interests[1]
		interestsList.add(inter2)

		val inter3 = CaptionInterestType()
		inter3.imgName = "Cinema"
		inter3.imgIcon = interests[2]
		interestsList.add(inter3)

		val inter4 = CaptionInterestType()
		inter4.imgName = "Game"
		inter4.imgIcon = interests[3]
		interestsList.add(inter4)

		val inter5 = CaptionInterestType()
		inter5.imgName = "Travel"
		inter5.imgIcon = interests[4]
		interestsList.add(inter5)

		val inter6 = CaptionInterestType()
		inter6.imgName = "Sports"
		inter6.imgIcon = interests[5]
		interestsList.add(inter6)

		val inter7 = CaptionInterestType()
		inter7.imgName = "Entertainment"
		inter7.imgIcon = interests[6]
		interestsList.add(inter7)

		val inter8 = CaptionInterestType()
		inter8.imgName = "Education"
		inter8.imgIcon = interests[7]
		interestsList.add(inter8)
	}

	override fun passData(interByte : String) {
		captionDataPass.onCaptionDataPass(
			binding.captionEditText.text.toString(),
			binding.tag1EditText.text.toString(),
			binding.tag2EditText.text.toString(),
			binding.tag3EditText.text.toString(),
			binding.tag4EditText.text.toString(),
			binding.tag5EditText.text.toString(),
			interByte
		)
	}

	override fun checkEmptyFields() {
		if (binding.captionEditText.text.toString().isEmpty()) {
			binding.captionInputLayout.error = context.getString(R.string.error_empty_caption)
			binding.captionInputLayout.requestFocus()
		} else if (interestsCheck == "00000000") {
			showSnackbar(binding.root, context.getString(R.string.error_pickInt))
		} else {
			fragmentManager?.popBackStack()
		}
		captionPresenter.getTags(
			binding.tag1EditText,
			binding.tag2EditText,
			binding.tag3EditText,
			binding.tag4EditText,
			binding.tag5EditText
		)
	}

	override fun onStart() {
		super.onStart()
		val closeIndicator = requireContext().getDrawableCompat(R.drawable.ic_cancel)?.apply {
			setColorFilter(
				requireContext().getColorCompat(R.color.red),
				PorterDuff.Mode.SRC_ATOP
			)
		}
		(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(closeIndicator)
		requireActivity().window.apply {
			// Update statusbar color to match toolbar color.
			statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
			// decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
		}
	}

	override fun onStop() {
		super.onStop()
		requireActivity().window.apply {
			// Reset statusbar color.
			statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
			decorView.systemUiVisibility = 0
		}
	}

	interface CaptionOnDataPass {
		fun onCaptionDataPass(
			caption : String,
			tag1 : String,
			tag2 : String,
			tag3 : String,
			tag4 : String,
			tag5 : String,
			interByte : String
		)
	}

	override fun onAttach(context : Context) {
		super.onAttach(context)
		captionDataPass = context as CaptionOnDataPass
	}

	companion object {
		private const val mTag = "CaptionFragment"
	}
}