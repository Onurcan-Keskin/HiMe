package com.huawei.hime.livestreamStreamaxia.livestream

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.presenters.LiveChatStreamaxiaContract
import com.huawei.hime.ui.presenters.LiveChatStreamaxiaPresenter
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.showLogError
import com.huawei.hime.util.showLogInfo
import com.streamaxia.android.CameraPreview
import com.streamaxia.android.StreamaxiaPublisher
import com.streamaxia.android.handlers.EncoderHandler
import com.streamaxia.android.handlers.RecordHandler
import com.streamaxia.android.handlers.RtmpHandler
import com.streamaxia.android.utils.Size
import main.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.SocketException
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class LiveStreamaxiaFragment : Fragment(),
    RtmpHandler.RtmpListener,
    RecordHandler.RecordListener,
    EncoderHandler.EncodeListener,
    LiveChatStreamaxiaContract {

    private lateinit var mView: View
    private var context: FragmentActivity? = null
    private var mPublisher: StreamaxiaPublisher? = null

    private val timeDate = DateFormat.getDateTimeInstance().format(Date())
    private val timeStamp = System.currentTimeMillis().toString()

    private var recPath = Environment.getExternalStorageDirectory().path + "/Pictures/HiMe"
    private var suffix = ".mp4"
    private var title: String? = ""
    private var videoTitle: String? = ""
    private var videoCover: String? = ""

    private var rtmpUrl: String? = ""
    private var mProgress: ProgressDialog? = null


    /* Camera */
    // private var clickCount = 0
    // private var startTime: Long? = null
    // private var duration: Long? = null
    // private val maxDuration = 500
    // private var camID = Camera.CameraInfo.CAMERA_FACING_FRONT

    private var streamaxiaCameraPreview: CameraPreview? = null
    private var streamaxiaStartStop: Button? = null
    private var streamaxiaStateText: TextView? = null
    private var streamaxiaTitle: TextView? = null
    private var streamaxiaLottieLayout: LinearLayout? = null
    private var streamaxiaLottie: LottieAnimationView? = null
    private var streamaxiaPlayerContentView: RelativeLayout? = null
    private var streamaxiaChronometer: Chronometer? = null

    private lateinit var currentID: String
    private lateinit var mDatabaseRef: DatabaseReference
    private lateinit var mLiveDatabaseReference: DatabaseReference
    private lateinit var mStorageVidRef: StorageReference
    private lateinit var mStorageImRef: StorageReference

    private var elpsTime: String? = ""

    private var pushVideoKey: String? = ""

    private var userVideoFeedRef: String? = ""
    private var liveStreamsRef: String? = ""

    private val liveChatStreamaxiaPresenter: LiveChatStreamaxiaPresenter by lazy {
        LiveChatStreamaxiaPresenter(
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        liveChatStreamaxiaPresenter.beforeViewsCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        liveChatStreamaxiaPresenter.beforeViewsCreate()

        mView = inflater.inflate(R.layout.fragment_live_streamaxia, container, false)

        liveChatStreamaxiaPresenter.onViewsCreate()

        streamaxiaStartStop!!.setOnClickListener {
            if (streamaxiaStateText!!.text.toString().toLowerCase(Locale.ROOT) == "[stopped]" ||
                streamaxiaStateText!!.text.toString().toLowerCase(Locale.ROOT) == "[disconnected]"
            ) {
                showDialog()
            } else if (streamaxiaStateText!!.text.toString()
                    .toLowerCase(Locale.ROOT) == "[connected]"
            ) {
                startStopStream()
                uploadTask()
            }
        }

        return mView
    }

    override fun onRtmpConnected(p0: String?) {
        setStatusMessage(p0!!)
        showLogInfo(mTAG,"onRtmpConnected")
        streamaxiaStartStop!!.text = "STOP"
    }

    override fun onRtmpIllegalStateException(p0: IllegalStateException?) {

    }

    override fun onRtmpStopped() {
        setStatusMessage("STOPPED")
    }

    override fun onRtmpIOException(p0: IOException?) {
        showLogError(mTAG, p0!!.toString())
    }

    override fun onRtmpAudioStreaming() {

    }

    override fun onRtmpSocketException(p0: SocketException?) {

    }

    override fun onRtmpDisconnected() {
        setStatusMessage("Disconnected")
    }

    override fun onRtmpVideoFpsChanged(p0: Double) {

    }

    override fun onRtmpConnecting(p0: String?) {
        setStatusMessage(p0!!)
    }

    override fun onRtmpAuthenticationg(p0: String?) {

    }

    override fun onRtmpVideoStreaming() {

    }

    override fun onRtmpAudioBitrateChanged(p0: Double) {

    }

    override fun onRtmpVideoBitrateChanged(p0: Double) {

    }

    override fun onRtmpIllegalArgumentException(p0: IllegalArgumentException?) {
        handleException(p0!!)
    }

    override fun onRecordIOException(p0: IOException?) {
        handleException(p0!!)
    }

    override fun onRecordIllegalArgumentException(p0: IllegalArgumentException?) {
        handleException(p0!!)
    }

    override fun onRecordFinished(p0: String?) {
        showLogDebug(mTAG, p0!!)
    }

    override fun onRecordPause() {

    }

    override fun onRecordResume() {

    }

    override fun onRecordStarted(p0: String?) {
        showLogDebug(mTAG,p0!!)
    }

    override fun onEncodeIllegalArgumentException(p0: IllegalArgumentException?) {
        handleException(p0!!)
    }

    override fun onNetworkWeak() {

    }

    override fun onNetworkResume() {

    }

    override fun initViews() {
        context = activity
        streamaxiaCameraPreview = mView.findViewById(R.id.streamaxia_camera_view)
        streamaxiaStartStop = mView.findViewById(R.id.streamaxia_start_stop)
        streamaxiaStateText = mView.findViewById(R.id.streamaxia_state_text)
        streamaxiaTitle = mView.findViewById(R.id.streamaxia_title)
        streamaxiaLottieLayout = mView.findViewById(R.id.streamaxia_lottie_lyt)
        streamaxiaLottie = mView.findViewById(R.id.streamaxia_lottie)
        streamaxiaPlayerContentView = mView.findViewById(R.id.streamaxia_PlayerContentView)
        streamaxiaChronometer = mView.findViewById(R.id.streamaxia_chronometer)

        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/HiMe")
        path.mkdirs()

        mProgress = ProgressDialog(context)
        mPublisher = StreamaxiaPublisher(streamaxiaCameraPreview, context)
        mPublisher!!.setEncoderHandler(EncoderHandler(this))
        mPublisher!!.setRtmpHandler(RtmpHandler(this))
        mPublisher!!.setRecordEventHandler(RecordHandler(this))
        streamaxiaCameraPreview!!.startCamera()

        showLogDebug(
            mTAG,
            streamaxiaCameraPreview!!.cameraFacing.toString() + " , " + streamaxiaCameraPreview!!.cameraDistance
        )

        setStreamerDefaultValues()
    }

    override fun initDB() {
        currentID = AppUser.getUserId()
        mStorageVidRef =
            FirebaseStorage.getInstance().reference.child("User/Videos")
                .child(currentID)
                .child(timeStamp)
                .child("Video")
                .child("$timeStamp.mp4")

        mStorageImRef =
            FirebaseStorage.getInstance().reference.child("User/Videos")
                .child(currentID)
                .child(timeStamp)
                .child("Cover")
                .child("$timeStamp.jpeg")


        mDatabaseRef = FirebaseDBHelper.getUserInfo(currentID)

        userVideoFeedRef = "User Feed/Videos/$currentID"
        liveStreamsRef = "Live Streams/Videos"
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                context!!.applicationContext,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            stopStreaming()
            stopChronometer()
        } else {
            val intent = Intent(context!!.applicationContext, MainActivity::class.java)
            startActivity(intent)
            Snackbar.make(
                streamaxiaPlayerContentView!!,
                "You need to grant permissions in order to begin streaming.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPause() {
        super.onPause()
        streamaxiaCameraPreview!!.stopCamera()
        mPublisher!!.stopPublish()
    }

    override fun onDestroy() {
        super.onDestroy()
        streamaxiaCameraPreview!!.stopCamera()
        mPublisher!!.stopPublish()
    }

    override fun stopChronometer() {
        val elapsed = SystemClock.elapsedRealtime()
        streamaxiaChronometer!!.base = elapsed
        showLogDebug(mTAG, streamaxiaChronometer!!.text.toString())
        streamaxiaChronometer!!.setOnChronometerTickListener {
            elpsTime = streamaxiaChronometer!!.text.toString()
            if (elpsTime == "00:04") {
                streamaxiaLottieLayout!!.visibility = View.GONE
            }
        }
        streamaxiaChronometer!!.stop()
    }

    override fun handleException(e: Exception) {
        try {
            Toast.makeText(context!!.applicationContext, e.message, Toast.LENGTH_SHORT).show()
            mPublisher!!.stopPublish();
        } catch (e1: Exception) {

        }
    }

    override fun setStreamerDefaultValues() {
        val sizes: MutableList<Size> =
            mPublisher!!.getSupportedPictureSizes(resources.configuration.orientation)
        val resolution: Size = sizes[0]
        mPublisher!!.setVideoOutputResolution(
            resolution.width,
            resolution.height,
            this.resources.configuration.orientation
        )
    }

    private fun setStatusMessage(msg: String) {
        activity!!.runOnUiThread(Runnable {
            streamaxiaStateText!!.text = "[$msg]"
        })
    }

    override fun stopStreaming() {
        mPublisher!!.stopPublish()
        mPublisher!!.stopRecord()
    }

    override fun takeSnapshot() {
        mLiveDatabaseReference = FirebaseDBHelper.getLiveFeed(pushVideoKey!!)
        val handler = Handler()
        handler.postDelayed({
            streamaxiaCameraPreview!!.takeSnapshot { image ->
                showLogDebug(mTAG, image.toString())
                val baos = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val thumbByte = baos.toByteArray()
                val uploadRef = mStorageImRef.putBytes(thumbByte)
                uploadRef.addOnCompleteListener { thumbTask ->
                    if (thumbTask.isSuccessful) {
                        mStorageImRef.downloadUrl.addOnSuccessListener { uri: Uri? ->
                            videoCover = uri.toString()
                            mLiveDatabaseReference.child("live_cover")
                                .setValue(videoCover)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val updateMap: MutableMap<String, Any> = HashMap()
                                        updateMap["live_cover"] = videoCover!!
                                        mLiveDatabaseReference.updateChildren(updateMap)
                                    }
                                }
                        }
                    } else {
                        showLogError(mTAG, "Update Video Cover - Thumb: Failed")
                    }
                }
            }
        }, 5000) //When Chron hits 5 seconds
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mPublisher!!.setScreenOrientation(newConfig.orientation)
    }

    override fun startStopStream() {
        if (streamaxiaStartStop!!.text.toString().toLowerCase(Locale.ROOT) == "start") {
            setStatusMessage("Stopped")
            streamaxiaStartStop!!.text = "STOP"
            streamaxiaChronometer!!.base = SystemClock.elapsedRealtime()
            streamaxiaChronometer!!.start()
            rtmpUrl = "rtmp://rtmp.streamaxia.com/streamaxia/${streamaxiaStreamName}_$currentID"
            mPublisher!!.startPublish(rtmpUrl)
            mPublisher!!.startRecord(recPath + "$title$suffix")
        } else {
            setStatusMessage("Connected")
            streamaxiaStartStop!!.text = "START"
            stopChronometer()
            mPublisher!!.stopPublish()
            mPublisher!!.stopRecord()
        }
    }

    override fun showDialog() {
        val dialog = Dialog(mView.context, R.style.BlurTheme)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.setContentView(R.layout.dialog_set_live_title)
        dialog.window!!.setGravity(Gravity.CENTER)
        val liveTitle = dialog.findViewById<TextInputEditText>(R.id.dialog_live_edt_txt)
        val save = dialog.findViewById<Button>(R.id.dialog_live_save)
        save.setOnClickListener {
            if (liveTitle.text.toString().isNotEmpty()) {
                streamaxiaTitle!!.text = liveTitle.text.toString()
                videoTitle = liveTitle.text.toString()
                title = liveTitle.text.toString() + "_${System.currentTimeMillis()}"
                startStopStream()
                liveDB()
                smileToCam()
                takeSnapshot()
                dialog.dismiss()
            } else {
                Snackbar.make(
                    save,
                    getText(R.string.error_empty_field), Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        dialog.show()
    }

    private fun smileToCam() {
        streamaxiaLottieLayout!!.bringToFront()
        streamaxiaLottieLayout!!.visibility = View.VISIBLE
        streamaxiaLottie!!.playAnimation()
        streamaxiaLottie!!.addAnimatorUpdateListener {
            it.repeatCount = 0
        }
    }

    override fun uploadTask() {
        showLogDebug(mTAG, recPath + "$title$suffix")
        mProgress = ProgressDialog(context!!.applicationContext)
        mProgress!!.setTitle(getString(R.string.prompt_uploadingVideo))
        mProgress!!.setMessage(getString(R.string.prompt_upladingVideo_message))
        mProgress!!.setIcon(R.drawable.ic_himelogo)
        mProgress!!.setCanceledOnTouchOutside(false)
        mProgress!!.show()
        val videoFile = File(recPath + "$title$suffix")
        val uri = Uri.fromFile(videoFile)
        try {
            val uploadTask = mStorageImRef.putFile(uri)
            uploadTask.continueWith {
                if (!it.isSuccessful) {
                    it.exception?.let { t -> throw  t }
                }
                mStorageVidRef.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {

                    userVideoFeedRef = "User Feed/Videos/$currentID"

                    it.result!!.addOnSuccessListener { task ->

                        /* User Feed/Videos/$currentID */
                        val videoMap: MutableMap<String, String> = HashMap()
                        videoMap["video_title"] = videoTitle.toString()
                        videoMap["video_lovely"] = "0"
                        videoMap["video_url"] = task.toString()
                        videoMap["video_seen"] = "0"
                        videoMap["video_upload_time"] = timeDate
                        videoMap["video_length"] = elpsTime.toString()
                        videoMap["video_cover"] = videoCover.toString()
                        videoMap["video_age_restriction"] = "0"
                        videoMap["shareStat"] = "1"

                        val mapVideo: MutableMap<String, Any> = HashMap()
                        mapVideo["$userVideoFeedRef/$pushVideoKey"] = videoMap

                        FirebaseDBHelper.rootRef().updateChildren(mapVideo)
                            .addOnCompleteListener { finalIt ->
                                if (finalIt.isSuccessful) {
                                    Log.d(mTAG, "-> $task")
                                    mProgress!!.dismiss()
                                } else {
                                    Toast.makeText(
                                        context!!.applicationContext,
                                        getText(R.string.error_video_upload),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    mProgress!!.dismiss()
                                }
                            }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                context!!.applicationContext,
                R.string.error_video_upload,
                Toast.LENGTH_SHORT
            ).show()
            mProgress!!.dismiss()
        }
    }

    override fun liveDB() {
        /* Live Streams/$pushVideoKey */
        val liveFeedPush = FirebaseDBHelper.rootRef()
            .child("Live Streams")
            .child("Videos").push()

        pushVideoKey = liveFeedPush.key.toString()

        val liveMap: MutableMap<String, String> = HashMap()
        liveMap["live_rtmp"] = rtmpUrl.toString()
        liveMap["live_lovely"] = "0"
        liveMap["live_seen"] = "0"
        liveMap["live_cover"] = "default"
        liveMap["live_upload_time"] = timeDate
        liveMap["live_uploader"] = currentID
        liveMap["live_title"] = videoTitle.toString()

        val mapLive: MutableMap<String, Any> = HashMap()
        mapLive["$liveStreamsRef/$pushVideoKey"] = liveMap
        Log.d(mTAG, "path: $liveStreamsRef/$pushVideoKey")
        FirebaseDBHelper.rootRef().updateChildren(mapLive)
    }

    companion object {
        private const val streamaxiaStreamName = "himeLiveChatBy"
        private var mTAG = LiveStreamaxiaFragment::class.java.simpleName
        private const val bitrate = 500
        private const val width = 1080
        private const val height = 1920
    }
}
