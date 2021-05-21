package com.huawei.hime.login

import android.content.Intent

interface LoginHelper {
    fun onLoginClick(loginType: LoginType)
    fun checkSilentSignIn()
    fun onDataReceived(requestCode: Int, resultCode: Int, data: Intent?)
    fun setCallback(loginHelperCallback: LoginHelperCallback)
}

interface LoginHelperCallback {
    fun onLoginSuccess(loginUserData: LoginUserData, loginUserInfoData: LoginUserInfoData)
    fun onLoginFail(errorMessage: String)
    fun updateUserDB(loginUserData: LoginUserData)
    fun updateUserInfoDB(loginUserInfoData: LoginUserInfoData)
    fun onSilentSignInFail()
    fun redirectToEmail()
    fun redirectToSignIn(signInIntent: Intent, requestCode: Int)
    fun onSilentSignInSuccess(
        loginUserData: LoginUserData,
        loginInfoData: LoginUserInfoData
    )

    fun onSilentSignInSuccess(loginUserData: LoginUserData)
}


enum class LoginType(val displayName: String) {
    FACEBOOK("Facebook"),
    GOOGLE_HUAWEI("Google or Huawei"),
    GOOGLE("Google"),
    HUAWEI("Huawei"),
    EMAIL("Email")
}

data class LoginUserData(
    val idToken: String,
    val userId: String,
    val loginType: LoginType,
    val onlineStatus: String
)

data class LoginUserInfoData(
    val nameSurname: String,
    val email: String,
    val userId: String,
    val photoUrl: String,
    val thumbUrl: String,
    val lovely: String,
    val memory: String,
    val age: String,
    val interests: String,
    val privacy: String
)