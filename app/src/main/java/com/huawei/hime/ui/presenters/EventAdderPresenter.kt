package com.huawei.hime.profile.profileevent

import android.content.Context
import android.media.MediaPlayer
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.card.MaterialCardView
import com.huawei.hime.R
import com.huawei.hime.ui.fragments.EventPickPhotoFragment
import com.huawei.hime.profile.profileevent.takePhoto.EventTakePhotoFragment
import com.huawei.hime.ui.activities.EventAdderActivity
import com.huawei.hime.util.showLogDebug
import com.huawei.hime.util.showLogError
import com.huawei.hime.util.showSnackbar
import com.ramotion.circlemenu.CircleMenuView

class EventAdderPresenter constructor(private val eventAdderContract : EventAdderContract) {

	fun onViewsCreate(){
		eventAdderContract.initViews()
	}

	fun menuGallery(
		context : Context,
		materialCardView : MaterialCardView,
		circleMenuView : CircleMenuView,
		constraintLayout : ConstraintLayout,
		activity : EventAdderActivity
	) {
		val mpMenuAppear = MediaPlayer.create(context.applicationContext, R.raw.menu_appear)
		val mpMenuClose = MediaPlayer.create(context.applicationContext, R.raw.menu_close)
		val mpMenuOpen = MediaPlayer.create(context.applicationContext, R.raw.menu_open)
		val mpMenuItemSelect = MediaPlayer.create(context.applicationContext, R.raw.menu_item_click)

		val fragment = activity.supportFragmentManager
		val fTakePhoto = EventTakePhotoFragment()
		val fPickPhotoFragment = EventPickPhotoFragment()

		materialCardView.setOnClickListener {
			showLogDebug("Clicked: Gallery", "success")
			circleMenuView.visibility = View.VISIBLE
			circleMenuView.bringToFront()
			mpMenuAppear.start()
			circleMenuView.eventListener = object : CircleMenuView.EventListener() {
				override fun onMenuOpenAnimationStart(view : CircleMenuView) {
					super.onMenuOpenAnimationStart(view)
					mpMenuOpen.start()
				}

				override fun onMenuOpenAnimationEnd(view : CircleMenuView) {
					super.onMenuOpenAnimationEnd(view)
				}

				override fun onMenuCloseAnimationStart(view : CircleMenuView) {
					super.onMenuCloseAnimationStart(view)
					mpMenuClose.start()
				}

				override fun onMenuCloseAnimationEnd(view : CircleMenuView) {
					super.onMenuCloseAnimationEnd(view)
					circleMenuView.visibility = View.GONE
				}

				override fun onButtonClickAnimationStart(view : CircleMenuView, buttonIndex : Int) {
					super.onButtonClickAnimationStart(view, buttonIndex)
					when (buttonIndex) {
						0 -> {
							mpMenuItemSelect.start()
							/* Take Photo */
							fragment.beginTransaction().run {
								return@run setCustomAnimations(
									R.anim.slide_in_up,
									R.anim.fade_out,
									R.anim.fade_in,
									R.anim.slide_out_down
								)
							}
								.add(R.id.eventFrame, fTakePhoto, fragment.javaClass.simpleName)
								.addToBackStack(fragment.javaClass.name)
								.commit()
						}
						1 -> {
							mpMenuItemSelect.start()
							fragment.beginTransaction().run {
								return@run setCustomAnimations(
									R.anim.slide_in_up,
									R.anim.fade_out,
									R.anim.fade_in,
									R.anim.slide_out_down
								)
							}
								.add(R.id.eventFrame, fPickPhotoFragment, fragment.javaClass.simpleName)
								.addToBackStack(fragment.javaClass.name)
								.commit()
						}
						2 -> {
							mpMenuItemSelect.start()
							/* Add Cover */
						}
						else -> showLogError("CircleMenuView", "when index exception.")
					}
				}

				override fun onButtonClickAnimationEnd(view : CircleMenuView, buttonIndex : Int) {
					super.onButtonClickAnimationEnd(view, buttonIndex)
					circleMenuView.visibility = View.GONE
				}

				override fun onButtonLongClick(view : CircleMenuView, buttonIndex : Int) : Boolean {
					when (buttonIndex) {
						0 -> {
							/* Take Photo */
							showSnackbar(constraintLayout, context.getString(R.string.take_photo))
						}
						1 -> {
							/* Pick Photo */
							showSnackbar(
								constraintLayout,
								context.getString(R.string.pick_photos_from_gallery)
							)
						}
						2 -> {
							showSnackbar(constraintLayout, context.getString(R.string.add_cover))
						}
					}
					return super.onButtonLongClick(view, buttonIndex)
				}

				override fun onButtonLongClickAnimationStart(
					view : CircleMenuView,
					buttonIndex : Int
				) {
					super.onButtonLongClickAnimationStart(view, buttonIndex)
				}

				override fun onButtonLongClickAnimationEnd(
					view : CircleMenuView,
					buttonIndex : Int
				) {
					super.onButtonLongClickAnimationEnd(view, buttonIndex)
				}
			}
		}
	}
}

interface EventAdderContract {
 fun initViews()
}