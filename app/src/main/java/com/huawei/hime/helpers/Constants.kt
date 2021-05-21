package com.huawei.hime.helpers

import android.content.Context
import android.os.Environment
import com.huawei.hime.appuser.AppUser
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Constants {
	companion object{
		/* Firebase constants */
		val fUserInfoRef = FirebaseDBHelper.getUserInfo(AppUser.getUserId())
		val fOnlineDBRef = FirebaseDBHelper.getUser(AppUser.getUserId())

		/* Recording Video */
		const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS" //"yyyy-MM-dd-HH-mm-ss-SSS"
		const val VIDEO_EXTENSION = ".mp4"
		const val IMAGE_EXTENSION = ".jpeg"
		const val USE_FRAME_PROCESSOR = true
		const val DECODE_BITMAP = false
		var recPath = Environment.getExternalStorageDirectory().path + "/Pictures/ExoVideoReference"

		fun getOutputDirectory(context: Context): File {
			val appContext = context.applicationContext
			val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
				File(
					recPath
				).apply { mkdirs() }
			}
			return if (mediaDir != null && mediaDir.exists()) mediaDir else appContext.filesDir
		}

		fun createFile(baseFolder: File, format: String, extension: String) =
			File(
				baseFolder, SimpleDateFormat(format, Locale.ROOT)
					.format(System.currentTimeMillis()) + extension
			)

	}
}