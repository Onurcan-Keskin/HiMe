package com.huawei.hime.login

import android.content.Intent
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.FirebaseDBHelper

class LoginPresenter constructor(
    private val loginContract: LoginContract,
    private val loginHelper: LoginHelper
) : LoginHelperCallback {

    fun onViewCreate() {
        loginContract.initViews()
        loginHelper.setCallback(this)
        loginHelper.checkSilentSignIn()
    }

    fun onEmailBtnClick() {
        //loginContract.redirectToGdpr()
        loginHelper.onLoginClick(LoginType.EMAIL)
    }

    fun onSignInGHClick() {
        loginHelper.onLoginClick(LoginType.GOOGLE_HUAWEI)
    }

    fun onFacebookSignInClick() {
        loginHelper.onLoginClick(LoginType.FACEBOOK)
    }

    override fun onLoginSuccess(
        loginUserData: LoginUserData,
        loginUserInfoData: LoginUserInfoData
    ) {
        AppUser.setUserId(loginUserData.userId)
        updateUserDB(loginUserData)
        updateUserInfoDB(loginUserInfoData)
    }

    override fun onLoginFail(errorMessage: String) {

    }

    override fun onSilentSignInSuccess(loginUserData: LoginUserData) {
        AppUser.setUserId(loginUserData.userId)
        loginContract.redirectToMain()
    }

    override fun onSilentSignInFail() {

    }

    override fun redirectToEmail() {

    }

    override fun redirectToSignIn(signInIntent: Intent, requestCode: Int) {
        loginContract.redirectToSignIn(signInIntent, requestCode)
    }

    override fun onSilentSignInSuccess(
        loginUserData: LoginUserData,
        loginInfoData: LoginUserInfoData
    ) {
    }

    override fun updateUserDB(loginUserData: LoginUserData) {
        val userDatabaseReference = FirebaseDBHelper.getUser(loginUserData.userId)
        val userMap = HashMap<String, String>()
        userMap["tokenID"] = loginUserData.userId
        userMap["signInMethod"] = loginUserData.loginType.displayName

        userDatabaseReference.setValue(userMap).addOnCompleteListener { taskG ->
            if (taskG.isSuccessful) {
                loginContract.redirectToGdpr()
            } else {
                loginContract.showUpdateMessage("on update fail")
            }
        }
    }

    override fun updateUserInfoDB(loginUserInfoData: LoginUserInfoData) {
        val userInfoDatabaseRef = FirebaseDBHelper.getUserInfo(loginUserInfoData.userId)
        val userInfoMap = HashMap<String, String>()
        userInfoMap["email"] = loginUserInfoData.email
        userInfoMap["nameSurname"] = loginUserInfoData.nameSurname
        userInfoMap["photoUrl"] = loginUserInfoData.photoUrl
        userInfoMap["age"] = "0"
        userInfoMap["lovely"] = "0"
        userInfoMap["memory"] = "Hello there, I am using HiMe."
        userInfoMap["onlineStatus"] = "Offline"
        userInfoMap["thumbUrl"] = "default"
        userInfoMap["interests"] = "00000000"
        userInfoMap["privacy"] = "1111"

        userInfoDatabaseRef.setValue(userInfoMap).addOnCompleteListener { taskGI ->
            if (taskGI.isSuccessful) {
                loginContract.showUpdateMessage("on update success")
            } else {
                loginContract.showUpdateMessage("on update fail")
            }
        }
    }

}

interface LoginContract {
    fun initViews()
    fun redirectToGdpr()
    fun redirectToSignIn(signInIntent: Intent, requestCode: Int)
    fun redirectToMain()
    fun restartApp()
    fun showUpdateMessage(message: String)
}