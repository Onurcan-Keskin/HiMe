package com.huawei.hime.profile.profiletravel

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.huawei.hime.R
import com.huawei.hime.appuser.AppUser
import com.huawei.hime.helpers.FirebaseDBHelper
import com.huawei.hime.ui.activities.MapActivity
import com.huawei.hime.util.ProfileTravelDataClass
import com.huawei.hime.util.showLogDebug

class ProfileTravelFragment : Fragment(), ProfileTravelContractor {

    private var mView: View? = null
    private var mRecycler: RecyclerView? = null
    private var mAddMap: ImageButton? = null
    private lateinit var context: FragmentActivity
    private lateinit var mCountrySpinner: Spinner

    /* vars */
    private val currentID = AppUser.getUserId()
    private lateinit var mUserFeedCountryRef: DatabaseReference
    private lateinit var mUserFeedTravelRef: DatabaseReference
    private lateinit var mUserInfoRef: DatabaseReference
    private lateinit var mLottieLayout: LinearLayout

    private var posterName: String? = ""
    private var posterImage: String? = ""

    private var countryFromSpinner: String? = "" //Default Spinner

    private var shareStat = ""
    private var mapPic = ""
    private var commentsAllowed = ""

    private var mCountryArray: MutableList<String> = ArrayList()

    private val profileTravelPresenter: ProfileTravelPresenter by lazy {
        ProfileTravelPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile_travel, container, false)
        profileTravelPresenter.onCreateViews()

        mAddMap!!.setOnClickListener {
            profileTravelPresenter.addTravel()
        }

        if (!(countryFromSpinner == null || countryFromSpinner == "")) {
            countryFromSpinner = "Turkey"
            mUserFeedCountryRef = FirebaseDBHelper.getUserFeedRootRef()
                .child("Travel")
                .child("/$currentID")
                .child("/$countryFromSpinner")
            showLogDebug(mTAG, "CountryDb : $countryFromSpinner")
            setupViewsWithRecycler()
        }

        return mView
    }

    override fun onStart() {
        super.onStart()
    }

    override fun initViews() {
        context = activity!!
        mLottieLayout = mView!!.findViewById(R.id.p_travel_lottie_lyt)
        mRecycler = mView!!.findViewById(R.id.profile_travel_recycler)
        mAddMap = mView!!.findViewById(R.id.profile_travel_add_map)
        mCountrySpinner = mView!!.findViewById(R.id.prof_travel_country_spinner)

        mRecycler!!.layoutManager = LinearLayoutManager(context)
        mRecycler!!.setHasFixedSize(true)
        setCountrySpinner()
    }

    override fun initDB() {
        mUserInfoRef = FirebaseDBHelper.getUserInfo(currentID)

        mUserInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                posterName = p0.child("nameSurname").value.toString()
                posterImage = p0.child("photoUrl").value.toString()
            }
        })

        mUserFeedTravelRef = FirebaseDBHelper.getUserFeedRootRef()
            .child("Travel")
            .child("/$currentID")

        mUserFeedTravelRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    mLottieLayout.visibility = View.GONE
                    mRecycler!!.visibility = View.VISIBLE
                    for (postSnap in p0.children) {
                        val key = postSnap.key!!
                        mCountryArray.add(key)
                        showLogDebug(mTAG, "Country: $key")
                        setCountrySpinner()
                    }
                } else {
                    mRecycler!!.visibility = View.GONE
                    mLottieLayout.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun setCountrySpinner() {
        val spinnerArrayAdapter = context.applicationContext?.let {
            ArrayAdapter(
                it,
                R.layout.spinner_drop_down,
                mCountryArray
            )
        }
        mCountrySpinner.adapter = spinnerArrayAdapter

        mCountrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                countryFromSpinner = "Turkey"
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                countryFromSpinner = parent!!.getItemAtPosition(position).toString()
                mUserFeedCountryRef = FirebaseDBHelper.getUserFeedRootRef()
                    .child("Travel")
                    .child("/$currentID")
                    .child("/$countryFromSpinner")
                showLogDebug(mTAG, "CountryDb : $countryFromSpinner")
                setupViewsWithRecycler()
            }
        }
    }

    override fun setupViewsWithRecycler() {
        val options = FirebaseRecyclerOptions.Builder<ProfileTravelDataClass>()
            .setQuery(mUserFeedCountryRef, ProfileTravelDataClass::class.java).build()

        val adapterFire = object :
            FirebaseRecyclerAdapter<ProfileTravelDataClass, ProfileTravelViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ProfileTravelViewHolder {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.single_posted_travel_card, parent, false)
                return ProfileTravelViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: ProfileTravelViewHolder,
                position: Int,
                model: ProfileTravelDataClass
            ) {
                val lisResID = getRef(position).key
                if (model.travel_map_holder != "") {
                    holder.bindView(
                        model.footer,
                        model.travel_latitude,
                        model.travel_longitude,
                        model.travel_address,
                        model.travel_map_holder,
                        model.tag1,
                        model.tag2,
                        model.tag3,
                        model.tag4,
                        model.tag5
                    )
                    holder.setPosterImage(posterImage!!)
                    holder.setPosterName(posterName!!)

                    holder.parent.setOnClickListener {
                        val feedDb =
                            FirebaseDBHelper.getTravelFeed(currentID, countryFromSpinner!!, lisResID!!)
                        feedDb.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                shareStat = p0.child("shareStat").value.toString()
                                commentsAllowed = p0.child("commentsAllowed").value.toString()
                                mapPic = p0.child("travel_map_holder").value.toString()

                                showLogDebug(mTAG, "$shareStat, $countryFromSpinner, $mapPic")
                            }
                        })

                        if (shareStat == "1") {
                            val uploadDb = FirebaseDBHelper.rootRef().child("uploads").child("Shareable")
                                .child(lisResID)
                            holder.setCircleMenu(
                                context,
                                uploadDb,
                                feedDb,
                                shareStat,
                                commentsAllowed,
                                lisResID
                            )
                        } else if (shareStat == "0") {
                            val uploadDb = FirebaseDBHelper.rootRef().child("uploads").child("NShareable")
                                .child(lisResID)
                            holder.setCircleMenu(
                                context,
                                uploadDb,
                                feedDb,
                                shareStat,
                                commentsAllowed,
                                lisResID
                            )
                        }
                        showLogDebug(mTAG, "$currentID $countryFromSpinner $lisResID")
                        showLogDebug(mTAG, "ShareStat $shareStat\nComments $commentsAllowed")
                    }
                }
            }
        }
        adapterFire.startListening()
        mRecycler!!.adapter = adapterFire
    }

    override fun startAddingTravel() {
        startActivity(Intent(this.activity, MapActivity::class.java))
    }

    companion object {
        private const val mTAG = "ProfileTravel"
    }


}