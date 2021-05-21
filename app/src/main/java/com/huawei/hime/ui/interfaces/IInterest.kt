package com.huawei.hime.ui.interfaces

class IInterest {
	interface ViewInterest{
		fun initViews()
		fun directToMain()
		fun populateDB(interestByte: String)
		fun checkEmptyFields()
		fun populateGrid()
		fun showUpdateLog(message: String)
	}
}