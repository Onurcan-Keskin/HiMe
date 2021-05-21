package liveChat

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.huawei.hime.R
import com.huawei.hime.voipcaas.VoipActivity
import com.huawei.hime.voipcaas.VoipHelper
import liveChat.caaskit.CaasKitView
import liveChat.caaskitinit.CaasKitHelper
import java.io.File

class HmsGmsBroadcastHelper constructor(private val context: VoipActivity) : VoipHelper {

    private lateinit var view: View
    var mVideoView: CaasKitView? = null
    private lateinit var mRootView: RelativeLayout
    private lateinit var mVideoControl: Button
    private lateinit var mVideoPick: Button
    private lateinit var mCallView: View
    private var mVideoFilePath: File? = null

    override fun setButtonClickEvent() {
        Log.d(mTAG, "setButtonClickEvent")
        mVideoControlEvent()
        mCallShowEvent()
        mCallHideEvent()
//        mVideoPick.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(intent, REQUEST_MEDIA_DATA)
//        }

    }

    override fun setVideoFilePath(intent: Intent) {
        val uri: Uri? = intent.data
        var cursor: Cursor? = null
        try {
            val filePathColumn =
                arrayOf(MediaStore.Video.Media.DATA)
            cursor = context.contentResolver.query(
                uri!!,
                filePathColumn, null, null, null
            )
            if (cursor == null) {
                return
            }
            cursor.moveToFirst()
            val filePath: String = cursor.getString(0)
            Toast.makeText(this.context, "FILE PATH: $filePath", Toast.LENGTH_SHORT).show()
            mVideoFilePath = File(filePath)
        } finally {
            cursor?.close()
        }
    }

    override fun startPlaying() {
        if (mVideoFilePath != null) {
            val isVideoPlaying: Boolean = mVideoView!!.startPlaying(mVideoFilePath)
            if (isVideoPlaying) {
                mVideoPick.visibility = View.GONE
                mVideoControl.visibility = View.GONE
                mCallView.visibility = View.GONE
            }
        }
    }

    override fun onMediaPause() {
        Log.d(mTAG, "onMediaPause")
    }

    override fun onMediaStop() {
        Log.d(mTAG, "onMediaStop")
        if (mVideoView != null) {
            mVideoView!!.onPause()
        }
        CaasKitHelper.instance!!.caasKitRelease()
    }

    override fun onMediaResume() {
        Log.d(mTAG, "onMediaResume")
        CaasKitHelper.instance!!.caasKitInit()
        if (mVideoView != null) {
            mVideoView!!.onResume()
        }
    }

    override fun onMediaDestroy() {
        Log.d(mTAG, "onMediaDestroy")
        if (mVideoView != null) {
            mVideoView!!.onDestroy()
        }
    }

    override fun addVideoView(context: Context) {
        if (mVideoView == null) {
            Log.d(mTAG, "addVideoView")
            mVideoView = CaasKitView(this.context)
            val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            params.gravity = Gravity.CENTER
            mRootView.addView(mVideoView, params)
            mVideoControl.visibility = View.VISIBLE
        }
    }

    override fun mVideoControlEvent() {
        if (mVideoView != null) {
            startPlaying()
        }
    }

    override fun mCallShowEvent() {
        CaasKitHelper.instance!!.sendShow()
    }

    override fun mCallHideEvent() {
        CaasKitHelper.instance!!.sendHide()
    }

    override fun onHmsGmsBroadcastHelperCreate() {
        view = context.findViewById(R.id.live_layout_view) as View
        //mVideoView = view.findViewById(R.id.lv_broadcast) as CaasKitView
        mRootView = context.findViewById(R.id.live_chat_rltv) as RelativeLayout
        mVideoControl = context.findViewById(R.id.live_chat_video_control) as Button
        mVideoPick = context.findViewById(R.id.live_chat_video_pick) as Button
        mCallView = context.findViewById(R.id.live_chat_call_view) as View
    }

    companion object {
        private const val mTAG = "CaasKitActivity"
    }
}