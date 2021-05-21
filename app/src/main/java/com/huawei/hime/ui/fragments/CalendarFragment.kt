package com.huawei.hime.ui.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.huawei.hime.R
import com.huawei.hime.databinding.CalendarDayBinding
import com.huawei.hime.databinding.CalendarHeaderBinding
import com.huawei.hime.databinding.FragmentCalendarBinding
import com.huawei.hime.ui.mvp.BaseFragmentExtend
import com.huawei.hime.ui.mvp.HasBackButton
import com.huawei.hime.ui.mvp.HasToolbar
import com.huawei.hime.util.*
import com.huawei.hime.util.views.*
import com.huawei.hime.util.views.getDrawableCompat
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class CalendarFragment : BaseFragmentExtend(R.layout.fragment_calendar), HasToolbar, HasBackButton {

    override val toolbar: Toolbar?
        get() = binding.exFourToolbar

    override val titleRes: Int? = null

    private val today = LocalDate.now()

    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null

    lateinit var calendarDataPassCalendar : CalendarOnDataPass

    private lateinit var context: FragmentActivity
    private lateinit var sharedPrefs: SharedPreferencesManager

    private val headerDateFormatter = DateTimeFormatter.ofPattern("EEE'\n'd MMM")

    private val startBackground: GradientDrawable by lazy {
        requireContext().getDrawableCompat(R.drawable.calendar_continuous_selected_bg_start) as GradientDrawable
    }

    private val endBackground: GradientDrawable by lazy {
        requireContext().getDrawableCompat(R.drawable.calendar_continuous_selected_bg_end) as GradientDrawable
    }

    private lateinit var binding: FragmentCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        context = activity!!
        sharedPrefs = SharedPreferencesManager(context)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding = FragmentCalendarBinding.bind(view)

        binding.exFourCalendar.post {
            val radius = ((binding.exFourCalendar.width / 7) / 2).toFloat()
            startBackground.setCornerRadius(topLeft = radius, bottomLeft = radius)
            endBackground.setCornerRadius(topRight = radius, bottomRight = radius)
        }

        val daysOfWeek = daysOfWeekFromLocale()
        binding.legendLayout.root.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                setTabColorPrefs(view,sharedPrefs)
            }
        }

        val currentMonth = YearMonth.now()
        binding.exFourCalendar.setup(currentMonth, currentMonth.plusMonths(12), daysOfWeek.first())
        binding.exFourCalendar.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val binding = CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH && (day.date == today || day.date.isAfter(
                            today
                        ))
                    ) {
                        val date = day.date
                        if (startDate != null) {
                            if (date < startDate || endDate != null) {
                                startDate = date
                                endDate = null
                            } else if (date != startDate) {
                                endDate = date
                            }
                        } else {
                            startDate = date
                        }
                        this@CalendarFragment.binding.exFourCalendar.notifyCalendarChanged()
                        bindSummaryViews()
                    }
                }
            }
        }

        binding.exFourCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.exFourDayText
                val roundBGView = container.binding.exFourRoundBgView

                textView.text = null
                textView.background = null
                roundBGView.makeInVisible()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.text = day.day.toString()

                    if (day.date.isBefore(today)) {
                        setTextColorPrefs(textView,sharedPrefs)
                    } else {
                        when {
                            startDate == day.date && endDate == null -> {
                                setTintColorPrefs(textView,sharedPrefs)
                                roundBGView.makeVisible()
                                roundBGView.setBackgroundResource(R.drawable.calendar_single_selected_bg)
                            }
                            day.date == startDate -> {
                                setTintColorPrefs(textView,sharedPrefs)
                                textView.background = startBackground
                            }
                            startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                                setTintColorPrefs(textView,sharedPrefs)
                                textView.setBackgroundResource(R.drawable.calendar_continuous_selected_bg_middle)
                            }
                            day.date == endDate -> {
                                setTintColorPrefs(textView,sharedPrefs)
                                textView.background = endBackground
                            }
                            day.date == today -> {
                                setTintColorPrefs(textView,sharedPrefs)
                                roundBGView.makeVisible()
                                roundBGView.setBackgroundResource(R.drawable.calendar_today_bg)
                            }
                            else -> setTintColorPrefs(textView,sharedPrefs)
                        }
                    }
                } else {
                    // This part is to make the coloured selection background continuous
                    // on the blank in and out dates across various months and also on dates(months)
                    // between the start and end dates if the selection spans across multiple months.

                    val startDate = startDate
                    val endDate = endDate
                    if (startDate != null && endDate != null) {
                        // Mimic selection of inDates that are less than the startDate.
                        // Example: When 26 Feb 2019 is startDate and 5 Mar 2019 is endDate,
                        // this makes the inDates in Mar 2019 for 24 & 25 Feb 2019 look selected.
                        if ((day.owner == DayOwner.PREVIOUS_MONTH &&
                                    startDate.monthValue == day.date.monthValue &&
                                    endDate.monthValue != day.date.monthValue) ||
                            // Mimic selection of outDates that are greater than the endDate.
                            // Example: When 25 Apr 2019 is startDate and 2 May 2019 is endDate,
                            // this makes the outDates in Apr 2019 for 3 & 4 May 2019 look selected.
                            (day.owner == DayOwner.NEXT_MONTH &&
                                    startDate.monthValue != day.date.monthValue &&
                                    endDate.monthValue == day.date.monthValue) ||

                            // Mimic selection of in and out dates of intermediate
                            // months if the selection spans across multiple months.
                            (startDate < day.date && endDate > day.date &&
                                    startDate.monthValue != day.date.monthValue &&
                                    endDate.monthValue != day.date.monthValue)
                        ) {
                            textView.setBackgroundResource(R.drawable.calendar_continuous_selected_bg_middle)
                        }
                    }
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView = CalendarHeaderBinding.bind(view).exFourHeaderText
        }
        binding.exFourCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    val monthTitle =
                        "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
                    container.textView.text = monthTitle
                }

                override fun create(view: View): MonthViewContainer = MonthViewContainer(view)
            }

        binding.exFourSaveButton.setOnClickListener click@{
            val startDate = startDate
            val endDate = endDate
            if (startDate != null && endDate != null) {
                val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
                val text = "Selected: ${formatter.format(startDate)} - ${formatter.format(endDate)}"
                val textPassed = "${formatter.format(startDate)} - ${formatter.format(endDate)}"
                val _endDate = formatter.format(endDate)
                val _startDate= formatter.format(startDate)
                calendarDataPassCalendar.onCalendarDataPass(textPassed,_startDate,_endDate)
                //Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(
                    requireView(),
                    getText(R.string.no_calendar_selection),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
            fragmentManager?.popBackStack()
        }

        bindSummaryViews()
    }

    private fun bindSummaryViews() {
        binding.exFourStartDateText.apply {
            if (startDate != null) {
                text = headerDateFormatter.format(startDate)
                setTextColor(Color.MAGENTA)
            } else {
                text = getString(R.string.start_date)
                setTextColor(Color.GRAY)
            }
        }
        binding.exFourEndDateText.apply {
            if (endDate != null) {
                text = headerDateFormatter.format(endDate)
                setTextColor(Color.MAGENTA)
            } else {
                text = getString(R.string.end_date)
                setTextColor(Color.GRAY)
            }
        }
        binding.exFourSaveButton.isEnabled =
            endDate != null || (startDate == null && endDate == null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.calendar_menu, menu)
        binding.exFourToolbar.post {
            // Configure menu text to match what is in the Airbnb app.
            binding.exFourToolbar.findViewById<TextView>(R.id.menuItemClear).apply {
                setTextColor(requireContext().getColorCompat(R.color.red))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                isAllCaps = false
            }
        }
        menu.findItem(R.id.menuItemClear).setOnMenuItemClickListener {
            startDate = null
            endDate = null
            binding.exFourCalendar.notifyCalendarChanged()
            bindSummaryViews()
            true
        }
    }

    override fun onStart() {
        super.onStart()
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

    override fun onStop() {
        super.onStop()
        requireActivity().window.apply {
            // Reset statusbar color.
            statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
            decorView.systemUiVisibility = 0
        }
    }

    interface CalendarOnDataPass{
        fun onCalendarDataPass(calendarTime : String, startTime: String, endTime:String)
    }

    override fun onAttach(context : Context) {
        super.onAttach(context)
        calendarDataPassCalendar=context as CalendarOnDataPass
    }

    companion object {
        private const val mTag = "CalendarFragment"
    }
}
