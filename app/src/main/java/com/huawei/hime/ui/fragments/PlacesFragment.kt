package com.huawei.hime.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.huawei.hime.R
import com.huawei.hime.databinding.FragmentPlacesBinding
import com.huawei.hime.ui.interfaces.IMap
import com.huawei.hime.profile.profileevent.places.PlacesContract
import com.huawei.hime.profile.profileevent.places.PlacesPresenter
import com.huawei.hime.ui.mvp.BaseFragmentExtend
import com.huawei.hime.ui.mvp.HasBackButton
import com.huawei.hime.ui.mvp.HasToolbar
import com.huawei.hime.util.views.getColorCompat
import com.huawei.hime.util.views.getDrawableCompat
import map.HmsGmsEventIMap

class PlacesFragment : BaseFragmentExtend(R.layout.fragment_places), HasToolbar, HasBackButton,
	PlacesContract {

	override val toolbar : Toolbar?
		get() = binding.placesToolbar

	override val titleRes : Int? = null

	private lateinit var mapView : View
	private lateinit var context : FragmentActivity

	private lateinit var binding : FragmentPlacesBinding

	private val hmsGmsIMap : IMap by lazy { HmsGmsEventIMap(this) }
	private val mapPresenter : PlacesPresenter by lazy { PlacesPresenter(this, hmsGmsIMap) }

	private val REQUEST_CODE_ASK_PERMISSIONS = 1

	private val RUNTIME_PERMISSIONS = arrayOf(
		Manifest.permission.ACCESS_FINE_LOCATION,
		Manifest.permission.WRITE_EXTERNAL_STORAGE,
		Manifest.permission.INTERNET,
		Manifest.permission.ACCESS_COARSE_LOCATION,
		Manifest.permission.ACCESS_WIFI_STATE,
		Manifest.permission.ACCESS_NETWORK_STATE
	)

	override fun onCreate(savedInstanceState : Bundle?) {
		context = activity!!
		super.onCreate(savedInstanceState)

	}

	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)
		binding = FragmentPlacesBinding.bind(view)
		hmsGmsIMap.onSavedInstanceBundle(savedInstanceState)
		hmsGmsIMap.listenCamera()
		hmsGmsIMap.onInitMap()

		binding.mapLayout.mapMap.setOnClickListener {
			hmsGmsIMap.onMapReady()
		}
		requestPermissions()
	}


	companion object {
		private const val mTag = "PlacesFragment"
	}

	private fun requestPermissions() {
		if (!hasPermissions()) {
			ActivityCompat.requestPermissions(
				context,
				RUNTIME_PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS
			)
		} else {
			Log.d("Permissions Granted: ", "All")
			hmsGmsIMap.onInitMap()
		}
	}


	private fun String.permissionGranted(ctx : Context) =
		ContextCompat.checkSelfPermission(ctx, this) == PackageManager.PERMISSION_GRANTED

	private fun hasPermissions() : Boolean {
		return RUNTIME_PERMISSIONS.count { !it.permissionGranted(context) } == 0
	}

	override fun onRequestPermissionsResult(
		requestCode : Int, permissions : Array<out String>,
		grantResults : IntArray
	) {
		when (requestCode) {
			REQUEST_CODE_ASK_PERMISSIONS -> {
				for (index in RUNTIME_PERMISSIONS.indices) {
					if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
						var notGrantedMessage =
							"Required permission ${permissions[index]} not granted."

						if (!ActivityCompat.shouldShowRequestPermissionRationale(
								context,
								permissions[index]
							)
						) {
							notGrantedMessage += "Please go to settings and turn on for sample app."
						}

						Toast.makeText(context, notGrantedMessage, Toast.LENGTH_LONG).show()
					}
				}
//                initMapFragmentView()
			}
			else -> {
				super.onRequestPermissionsResult(requestCode, permissions, grantResults)
			}
		}
	}

	override fun initViews() {
		mapView = binding.mapLayout.mapMap
		//binding.mapLayout.mapMap.onStart()
	}

	override fun onMapClick() {
		mapPresenter.onMapInteract()
	}

	override fun getLocationUpdate() {}

	override fun onStart() {
		super.onStart()
		hmsGmsIMap.onStartMap()

		val closeIndicator = requireContext().getDrawableCompat(R.drawable.ic_cancel)?.apply {
			setColorFilter(
				requireContext().getColorCompat(R.color.red),
				PorterDuff.Mode.SRC_ATOP
			)
		}
		(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(closeIndicator)
		requireActivity().window.apply {
			// Update statusbar color to match toolbar color.
			statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
			// decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
		}
	}

	override fun onResume() {
		super.onResume()
		//hmsGmsMapHelper.onResumeMap()
	}

	override fun onPause() {
		super.onPause()
		//hmsGmsMapHelper.onPauseMap()

	}

	override fun onDestroy() {
		super.onDestroy()
		//hmsGmsMapHelper.onDestroyMap()
	}

	override fun onStop() {
		super.onStop()
		requireActivity().window.apply {
			// Reset statusbar color.
			statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
			decorView.systemUiVisibility = 0
		}
	}

	override fun onLowMemory() {
		super.onLowMemory()
		//hmsGmsMapHelper.onLowMemoryMap()
	}
}