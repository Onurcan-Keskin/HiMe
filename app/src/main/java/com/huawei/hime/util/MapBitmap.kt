package com.huawei.hime.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.view.PixelCopy
import android.view.View
import com.huawei.hime.profile.profileevent.takePhoto.EventTakePhotoFragment
import com.pranavpandey.android.dynamic.utils.DynamicUnitUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun getBitmapFromView(view : View) : Bitmap? {
	val bitmap =
		Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
	val canvas = Canvas(bitmap)
	view.draw(canvas)
	val baos = ByteArrayOutputStream()
	bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
	return bitmap
}

fun getBitmapFromView(view : View, defaultColor : Int) : Bitmap? {
	val bitmap =
		Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
	val canvas = Canvas(bitmap)
	canvas.drawColor(defaultColor)
	view.draw(canvas)
	return bitmap
}

fun getBitmapFromView(view : View, activity : Activity, callback : (Bitmap) -> Unit) {
	activity.window?.let { window ->
		val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
		val locationOfViewInWindow = IntArray(2)
		view.getLocationInWindow(locationOfViewInWindow)
		try {
			PixelCopy.request(
                window,
                Rect(
                    locationOfViewInWindow[0],
                    locationOfViewInWindow[1],
                    locationOfViewInWindow[0] + view.width,
                    locationOfViewInWindow[1] + view.height
                ),
                bitmap,
                { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        callback(bitmap)
                    }
                    // possible to handle other result codes ...
                },
                Handler()
            )
		} catch (e : IllegalArgumentException) {
			// PixelCopy may throw IllegalArgumentException, make sure to handle it
			e.printStackTrace()
		}
	}
}

fun createBitmapFromView(view : View, width : Int, height : Int) : Bitmap {
	if (width > 0 && height > 0) {
		view.measure(
            View.MeasureSpec.makeMeasureSpec(
                DynamicUnitUtils
                    .convertDpToPixels(width.toFloat()), View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                DynamicUnitUtils
                    .convertDpToPixels(height.toFloat()), View.MeasureSpec.EXACTLY
            )
        )
	}
	view.layout(0, 0, view.measuredWidth, view.measuredHeight)
	val bitmap = Bitmap.createBitmap(
        view.measuredWidth,
        view.measuredHeight, Bitmap.Config.ARGB_8888
    )
	val canvas = Canvas(bitmap)
	view.background?.draw(canvas)
	view.draw(canvas)
	return bitmap
}

fun bitmapToFile(
    context : Context?,
    bitmap : Bitmap
) : File? { // File name like "image.png"
	//create a file to write bitmap data
	var file : File? = null
	return try {
		val format = "yyyy-MM-dd-HH-mm-ss-SSS"
		val extension = ".jpg"
		file =
			File(
                EventTakePhotoFragment.getOutputDirectory(context!!.applicationContext),
                SimpleDateFormat(format, Locale.ROOT)
                    .format(System.currentTimeMillis()) + extension
            )
		file.createNewFile()

//Convert bitmap to byte array
		val bos = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos) // YOU can also save it in JPEG
		val bitmapdata = bos.toByteArray()

//write the bytes in file
		val fos = FileOutputStream(file)
		fos.write(bitmapdata)
		fos.flush()
		fos.close()
		file
	} catch (e : Exception) {
		e.printStackTrace()
		file // it will return null
	}
}