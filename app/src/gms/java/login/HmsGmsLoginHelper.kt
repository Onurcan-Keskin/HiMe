package login

import android.content.Context
import android.content.Intent
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.installations.FirebaseInstallations
import com.huawei.hime.R
import com.huawei.hime.login.*
import com.huawei.hime.util.views.toSafeString

class HmsGmsLoginHelper constructor(private val context: Context) : LoginHelper {

    private val googleRequestCode = 1994
    private val facebookRequestCode = 1997
    private val logTag = "HmsGmsLoginHelper"

    /* FB */
    private val callbackManager = CallbackManager.Factory.create()

    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestId()
            .requestEmail()
            .build()
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(context, gso)
    }

    private lateinit var loginHelperCallback: LoginHelperCallback

    override fun setCallback(loginHelperCallback: LoginHelperCallback) {
        this.loginHelperCallback = loginHelperCallback
    }

    override fun onLoginClick(loginType: LoginType) {
        when (loginType) {
            LoginType.GOOGLE_HUAWEI -> onGoogleLogInClick()
            LoginType.FACEBOOK -> onFacebookLogInClick()
            LoginType.EMAIL -> onEmailLogInClick()
            else -> onGoogleLogInClick()
        }
    }

    private fun onGoogleLogInClick() {
        loginHelperCallback.redirectToSignIn(googleSignInClient.signInIntent, googleRequestCode)
    }

    private fun onEmailLogInClick() {
        loginHelperCallback.redirectToEmail()
    }

    private fun onFacebookLogInClick() {
        loginHelperCallback
    }

    override fun checkSilentSignIn() {
        checkGoogleSilentSignIn()
        checkFBSilentSignIn()
        checkEmailSilentSignIn()
    }

    private fun checkGoogleSilentSignIn() {
        googleSignInClient.silentSignIn().addOnSuccessListener { googleAccount ->
            firebaseAuthWithGoogle(googleAccount, isSilentLogIn = true)
        }.addOnCanceledListener {
            loginHelperCallback.onSilentSignInFail()
        }
    }

    private fun checkFBSilentSignIn() {

    }

    private fun checkEmailSilentSignIn(){

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount, isSilentLogIn: Boolean = false) {
        val auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val loginUserData = LoginUserData(
                        userId = user?.uid.toSafeString(),
                        loginType = LoginType.GOOGLE,
                        idToken = user?.uid.toSafeString(),
                        onlineStatus = "Offline"
                    )
                    val loginInfoData = LoginUserInfoData(
                        nameSurname = user?.displayName.toSafeString(),
                        email = user?.email.toSafeString(),
                        userId = user?.uid.toSafeString(),
                        photoUrl = user?.photoUrl.toString(),
                        lovely = "0",
                        memory = "Hello there, I am using HiMe.",
                        thumbUrl = "default",
                        age = "0",
                        interests = "00000000",
                        privacy = "1111"
                    )
                    Log.d("GoogleActivity.TAG", "SignInWithCredential:success")
                    if (isSilentLogIn) {
                        loginHelperCallback.onSilentSignInSuccess(loginUserData)
                    } else {
                        loginHelperCallback.onLoginSuccess(loginUserData, loginInfoData)
                    }

                } else {
                    loginHelperCallback.onLoginFail("Firebase Google Auth Fail")
                }
            }
    }

    private fun firebaseAuthWithFacebook(view: LoginButton, isSilentLogIn: Boolean = false) {
        val callback = CallbackManager.Factory.create()
        view.setReadPermissions("email", "public_profile")
        view.registerCallback(callback, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken, isSilentLogIn)
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {
                Log.d("firebaseAuthWithFacebook", error!!.printStackTrace().toString())
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken, isSilentLogIn: Boolean = false) {
        val auth = FirebaseAuth.getInstance()
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = auth.currentUser
                val loginUserData = LoginUserData(
                    userId = user?.uid.toSafeString(),
                    loginType = LoginType.FACEBOOK,
                    idToken = user?.uid.toSafeString(),
                    onlineStatus = "Offline"
                )
                val loginInfoData = LoginUserInfoData(
                    nameSurname = user?.displayName.toSafeString(),
                    email = user?.email.toSafeString(),
                    userId = user?.uid.toSafeString(),
                    photoUrl = user?.photoUrl.toString(),
                    lovely = "0",
                    memory = "Hello there, I am using HiMe.",
                    thumbUrl = "default",
                    age = "0",
                    interests = "00000000",
                    privacy = "1111"
                )
                if (isSilentLogIn) {
                    loginHelperCallback.onSilentSignInSuccess(loginUserData)
                } else {
                    loginHelperCallback.onLoginSuccess(loginUserData, loginInfoData)
                }
            } else {
                loginHelperCallback.onLoginFail("Firebase Facebook Login Failed")
                if (it.exception is FirebaseAuthUserCollisionException) {
                    loginHelperCallback.onLoginFail("Firebase Email Collision")
                } else {
                    loginHelperCallback.onLoginFail("Firebase Error")
                }
            }
        }
    }

    override fun onDataReceived(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            googleRequestCode -> onGoogleSignInDataReceived(data)
            facebookRequestCode -> callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onEmailSignInReceived(
        email: String,
        password: String,
        isSilentLogIn: Boolean = false
    ) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val current = FirebaseAuth.getInstance().currentUser
                    val uid = current!!.uid

                    FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val loginUserData = LoginUserData(
                                userId = current.uid.toSafeString(),
                                loginType = LoginType.EMAIL,
                                idToken = current.uid.toSafeString(),
                                onlineStatus = "Offline"
                            )
                            val loginInfoData = LoginUserInfoData(
                                nameSurname = current.displayName.toSafeString(),
                                email = current.email.toSafeString(),
                                userId = current.uid.toSafeString(),
                                photoUrl = current.photoUrl.toString(),
                                lovely = "0",
                                memory = "Hello there, I am using HiMe.",
                                thumbUrl = "default",
                                age = "0",
                                interests = "00000000",
                                privacy = "1111"
                            )
                            if (isSilentLogIn) {
                                loginHelperCallback.onSilentSignInSuccess(loginUserData)
                            } else {
                                loginHelperCallback.onLoginSuccess(loginUserData, loginInfoData)
                            }
                        } else {
                            loginHelperCallback.onLoginFail("Firebase Google Auth Fail")
                            if (task.exception is FirebaseAuthUserCollisionException) {
                                loginHelperCallback.onLoginFail("Firebase Email Collision")
                            } else {
                                loginHelperCallback.onLoginFail("Firebase Error")
                            }
                        }
                    }
                }
            }
    }

    private fun onGoogleSignInDataReceived(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account!!)
        } catch (e: ApiException) {
            Log.w(logTag, "Google SignIn Failed: ", e)
        }
    }

}