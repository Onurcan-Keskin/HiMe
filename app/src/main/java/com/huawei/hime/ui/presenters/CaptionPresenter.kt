package com.huawei.hime.ui.presenters

import com.google.android.material.textfield.TextInputEditText
import com.huawei.hime.models.CaptionInterestType
import java.util.*

class CaptionPresenter constructor(private val contract : CaptionContract) {

	fun onViewsCreate() {
		contract.initViews()
	}

	fun onSave(itemList: List<CaptionInterestType>){
		var interByte = ""
		itemList.forEachIndexed { _, captionInterestType ->
			interByte = if (captionInterestType.isSelectedChck){
				"${interByte}1"
			} else {
				"${interByte}0"
			}
		}
		contract.checkEmptyFields()
		contract.passData(interByte)
	}

	fun getTags(
		tagEdit1 : TextInputEditText,
		tagEdit2 : TextInputEditText,
		tagEdit3 : TextInputEditText,
		tagEdit4 : TextInputEditText,
		tagEdit5 : TextInputEditText
	) {
		if(tagEdit1.length() <= 1) null else {
			tagEdit1.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
		}
		if (tagEdit2.length() <= 1) null else {
			tagEdit2.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
		}
		if (tagEdit3.length() <= 1) null else {
			tagEdit3.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
		}
		if (tagEdit4.length() <= 1) null else {
			tagEdit4.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
		}
		if (tagEdit5.length() <= 1) null else {
			tagEdit5.text.toString().replace(" ", "_").toLowerCase(Locale.ROOT)
		}
	}
}

interface CaptionContract {
	fun initViews()
	fun checkEmptyFields()
	fun populateGrid()
	fun passData(interByte:String)
}