package com.fitsionary.momspt.presentation.daily.view

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.enum.DirectionEnum
import com.fitsionary.momspt.data.model.WeeklyStatisticsModel
import com.fitsionary.momspt.databinding.FragmentDayStatisticsBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.daily.viewmodel.DayStatisticsViewModel
import com.fitsionary.momspt.util.NavResult
import com.fitsionary.momspt.util.navResult
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import timber.log.Timber
import java.text.DecimalFormat


class DayStatisticsFragment :
    BaseFragment<FragmentDayStatisticsBinding, DayStatisticsViewModel>(R.layout.fragment_day_statistics) {
    override val viewModel: DayStatisticsViewModel by lazy {
        ViewModelProvider(this).get(DayStatisticsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        navResult(findNavController()) { result ->
            when (result) {
                is NavResult.Cancel -> Timber.i("cancel")
                is NavResult.Ok -> Timber.i("ok")
                is NavResult.TodayWeight -> {
                    viewModel.editTodayUserWeight(result.weight)
                }
                else -> Timber.i("unexpected result type")
            }
        }

        viewModel.run {
            getTodayUserStatistics()
            getWeeklyUserStatistics()
        }

        viewModel.weeklyStatistics.observe(viewLifecycleOwner, {
            graphSetting(it)
        })

        binding.btnEditWeight.setOnClickListener {
            findNavController().navigate(DailyFragmentDirections.actionMainDailyToCustomEditTodayWeightDialog())
        }
        binding.tvAnalysis.setOnClickListener {
            findNavController().navigate(
                DailyFragmentDirections.actionMainDailyToAnalysisFragment(
                    DirectionEnum.TO_DAILY
                )
            )
        }
    }

    private fun graphSetting(data: WeeklyStatisticsModel) {
        val labelList = arrayListOf("일", "월", "화", "수", "목", "금", "토")
        workoutTimeGraph(labelList, data.workoutTimeList)
        weightGraph(labelList, data.weightList)
        binding.chartWorkoutTime.setTouchEnabled(false)
        binding.chartWeight.setTouchEnabled(false)
    }

    private fun workoutTimeGraph(labelList: ArrayList<String>, valList: ArrayList<Int>) {
        val barEntries: ArrayList<BarEntry> = ArrayList()
        for (i in 0 until valList.size) {
            barEntries.add(BarEntry(valList[i].toFloat(), i))
        }

        val barDataSet = BarDataSet(barEntries, null)
        barDataSet.run {
            valueTextSize = 10f
            valueTypeface = ResourcesCompat.getFont(requireContext(), R.font.noto_sans)

            barSpacePercent = 40f
            color = ResourcesCompat.getColor(resources, R.color.pink, null)

            axisDependency = YAxis.AxisDependency.LEFT

            valueFormatter = TimeFormatter()
            valueTextColor = ResourcesCompat.getColor(resources, R.color.dark_gray, null)
        }

        binding.chartWorkoutTime.run {
            xAxis.textSize = 10f
            xAxis.typeface =
                ResourcesCompat.getFont(requireContext(), R.font.noto_sans)

            axisLeft.textSize = 10f
            axisLeft.typeface =
                ResourcesCompat.getFont(requireContext(), R.font.noto_sans)

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            legend.isEnabled = false
            setDescription(null)
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)

            axisRight.setAxisMinValue(0f)
            axisLeft.setAxisMinValue(0f)

            val barData = BarData(labelList, barDataSet)
            data = barData

            invalidate()
        }
    }

    private fun weightGraph(labelList: ArrayList<String>, valList: ArrayList<Double>) {
        val barEntries: ArrayList<BarEntry> = ArrayList()
        for (i in 0 until valList.size) {
            barEntries.add(BarEntry(valList[i].toFloat(), i))
        }

        val lineDataSet = LineDataSet(barEntries as List<Entry>?, null) // 변수로 받아서 넣어줘도 됨
        lineDataSet.run {
            valueTextSize = 10f
            valueTypeface = ResourcesCompat.getFont(requireContext(), R.font.noto_sans)

            axisDependency = YAxis.AxisDependency.LEFT
            lineWidth = 1f
            color = ResourcesCompat.getColor(resources, R.color.pink, null)

            setCircleColor(ResourcesCompat.getColor(resources, R.color.pink, null))
            setCircleColorHole(ResourcesCompat.getColor(resources, R.color.pink, null))

            valueFormatter = WeightFormatter()
            valueTextColor = ResourcesCompat.getColor(resources, R.color.dark_gray, null)
        }

        binding.chartWeight.run {
            xAxis.textSize = 10f
            xAxis.typeface =
                ResourcesCompat.getFont(requireContext(), R.font.noto_sans)

            axisLeft.textSize = 10f
            axisLeft.typeface =
                ResourcesCompat.getFont(requireContext(), R.font.noto_sans)

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            legend.isEnabled = false
            setDescription(null)
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)

            val lineData = LineData(labelList, lineDataSet) // 라이브러리 v3.x 사용하면 에러 발생함
            data = lineData

            invalidate()
        }
    }

    inner class TimeFormatter : ValueFormatter {
        private val mFormat: DecimalFormat = DecimalFormat("#")

        override fun getFormattedValue(
            value: Float,
            entry: Entry?,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler?
        ): String {
            return mFormat.format(value).toString() + "분"
        }

    }

    inner class WeightFormatter : ValueFormatter {
        private val mFormat: DecimalFormat = DecimalFormat("#.0")

        override fun getFormattedValue(
            value: Float,
            entry: Entry?,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler?
        ): String {
            return mFormat.format(value).toString() + "kg"
        }

    }
}