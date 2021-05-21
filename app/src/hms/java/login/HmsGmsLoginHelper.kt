package login

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.huawei.agconnect.auth.AGConnectAuthCredential
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.FacebookAuthProvider
import com.huawei.agconnect.auth.SignInResult
import com.huawei.hime.login.*
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.showLogError
import com.huawei.hime.util.views.toSafeString
import com.huawei.hmf.tasks.Task
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.result.AuthHuaweiId
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService

class HmsGmsLoginHelper constructor(private val context: Context) : LoginHelper {

    private val HUAWEI_REQUEST_SIGN_IN_LOGIN = 1002
    private val FACEBOOK_REQUEST_SIGN_IN_REQUEST = 1003

    private val _signInResult = MutableLiveData<SignInResult?>()

    private var mAuthParam: HuaweiIdAuthParams =
        HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setId()
            .setProfile()
            .setEmail()
            .createParams()

    /* FB login */
    private val fbCallbackManager = CallbackManager.Factory.create()

    private var mAuthManager: HuaweiIdAuthService =
        HuaweiIdAuthManager.getService(context, mAuthParam)

    private lateinit var loginHelperCallback: LoginHelperCallback

    override fun setCallback(loginHelperCallback: LoginHelperCallback) {
        this.loginHelperCallback = loginHelperCallback
    }

    //TODO function extends multi class
    private fun convertHuaweiAccountToLoginUserData(huaweiAccount: AuthHuaweiId): LoginUserData {
        return LoginUserData(
            idToken = huaweiAccount.unionId.toSafeString(),
            userId = huaweiAccount.unionId.toSafeString(),
            loginType = LoginType.HUAWEI,
            onlineStatus = "offline"
        )
    }

    override fun onLoginClick(loginType: LoginType) {
        loginHelperCallback.redirectToSignIn(
            mAuthManager.signInIntent,
            HUAWEI_REQUEST_SIGN_IN_LOGIN
        )
    }

    override fun checkSilentSignIn() {
        val task: Task<AuthHuaweiId> = mAuthManager.silentSignIn()
        task.addOnSuccessListener { huaweiAccount ->
            loginHelperCallback.onSilentSignInSuccess(
                convertHuaweiAccountToLoginUserData(
                    huaweiAccount
                )
            )
            Log.i(TAG, "silentSignIn success")
        }.addOnFailureListener { e ->
            loginHelperCallback.onSilentSignInFail()
            showLogError(TAG,e.message!!)
        }
    }

    fun authWithHuawei(credential: AGConnectAuthCredential){
        AGConnectAuth.getInstance().signIn(credential)
            .addOnSuccessListener { signInResult ->
                _signInResult.value = signInResult
            }.addOnFailureListener {
                Log.e(javaClass.simpleName,it.printStackTrace().toString())
            }
    }


    override fun onDataReceived(requestCode: Int, resultCode: Int, data: Intent?) {
        /* Huawei Handler */
        if (requestCode == HUAWEI_REQUEST_SIGN_IN_LOGIN) {
            //login success
//get user message by parseAuthResultFromIntent
            val authHuaweiIdTask =
                HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {
                val huaweiAccount = authHuaweiIdTask.result
                Log.i(TAG, huaweiAccount.displayName + " signIn success ")
                Log.i(
                    TAG, "AccessToken: " + huaweiAccount.accessToken
                )
                val loginUserData = LoginUserData(
                    userId = huaweiAccount?.unionId.toSafeString(),
                    idToken = huaweiAccount?.unionId.toSafeString(),
                    loginType = LoginType.HUAWEI,
                    onlineStatus = "Offline"
                )
                //val loginUserInfoData=authHuaweiIdTask.result
                val loginInfoData = LoginUserInfoData(
                    nameSurname = huaweiAccount?.displayName.toSafeString(),
                    email = huaweiAccount?.email.toSafeString(),
                    userId = huaweiAccount?.unionId.toSafeString(),
                    photoUrl = huaweiAccount?.avatarUriString.toString(),
                    thumbUrl = "default",
                    lovely = "0",
                    memory = "Hello there, I am using HiMe.",
                    age = "0",
                    interests = "00000000",
                    privacy = "1111"
                )
                Log.i(TAG, loginInfoData.email + " email address ")
                loginHelperCallback.onLoginSuccess(loginUserData, loginInfoData)
            } else {
                Log.i(
                    TAG,
                    "signIn failed: " + (authHuaweiIdTask.exception as ApiException).statusCode
                )
                loginHelperCallback.onLoginFail("On Login Fail")
            }
        }

        /* Facebook Handler */
        if (requestCode == FACEBOOK_REQUEST_SIGN_IN_REQUEST) {
            val facebookCallback = object: FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    val credential: AGConnectAuthCredential = FacebookAuthProvider.credentialWithToken(result?.accessToken?.token)
                    authWithHuawei(credential)
                }

                override fun onCancel() {
                    TODO("Not yet implemented")
                }

                override fun onError(error: FacebookException?) {
                    TODO("Not yet implemented")
                }

            }

        } else {

        }
    }

}