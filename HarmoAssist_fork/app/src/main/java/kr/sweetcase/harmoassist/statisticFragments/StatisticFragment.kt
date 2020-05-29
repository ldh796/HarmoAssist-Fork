package kr.sweetcase.harmoassist.statisticFragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Toast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import kr.sweetcase.harmoassist.R
import kr.sweetcase.harmoassist.dialogs.InfoDialog
import kr.sweetcase.harmoassist.listMaterials.Music
import java.lang.IndexOutOfBoundsException
import java.util.Map
import javax.xml.transform.Templates
import kotlin.collections.Map.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter as IndexAxisValueFormatter1

/**
 * A simple [Fragment] subclass.
 */
class StatisticFragment(val musicInfo : Music) : Fragment() {

    private lateinit var pitchChart : PieChart
    private lateinit var signatureChartMajor : HorizontalBarChart
    private lateinit var signatureChartMinor : HorizontalBarChart


    /** 이미지 버튼들**/
    lateinit var summaryInfoBtn : ImageButton
    lateinit var pitchHistogramInfoBtn : ImageButton
    lateinit var keySignatureHistogram : ImageButton

    private fun makeInfoDialog(id : Int, mainView : View) : Dialog {
        val str = getString(id)
        var point = Point()
        activity?.windowManager?.defaultDisplay?.getSize(point)
        return InfoDialog(mainView.context, str, point)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView : View = inflater.inflate(R.layout.fragment_statistic, container, false)

        pitchChart = mainView.findViewById(R.id.pitch_chart)
        signatureChartMajor = mainView.findViewById(R.id.signature_chart_major)
        signatureChartMinor = mainView.findViewById(R.id.signature_chart_minor)

        summaryInfoBtn = mainView.findViewById(R.id.statistic_summary_info_btn)
        pitchHistogramInfoBtn = mainView.findViewById(R.id.pitch_histogram_info_btn)
        keySignatureHistogram = mainView.findViewById(R.id.key_signature_histogram_info_btn)


        /** 정보 관련 버튼 리스너 **/
        summaryInfoBtn.setOnClickListener {
            makeInfoDialog(R.string.statistic_basic_summary, mainView).show()
        }
        pitchHistogramInfoBtn.setOnClickListener {
            makeInfoDialog(R.string.statistic_basic_summary, mainView).show()
        }
        keySignatureHistogram.setOnClickListener {
            makeInfoDialog(R.string.statistic_basic_summary, mainView).show()
        }

        /** 피치 분포도 **/
        // TODO Pitch 분포도 계산
        // 얘는 예시
        val pitchList = arrayListOf<Int>(19, 53, 60, 240, 13, 40, 50, 60, 120, 24, 56, 134)

        // TODO 데이터를 Pitch Pie Chart에 적용
        initPitchChart(pitchChart, pitchList)
        pitchChart.invalidate()

        /** 키 분포도 **/
        // TODO 키 시그니처 계산
        // 얘는 예시
        // 파라미터는 C부터 B 까지 메이저와 마이너를 구별
        val signatureListMajor = arrayListOf<Int>(
            4, 5, 0, 0, 0, 5, 0, 2, 4, 5, 6, 3
        )
        val signatureListMinor = arrayListOf<Int>(
            5, 2, 1, 4, 0, 0, 0, 5, 9, 1, 6, 0
        )

        // Bar Chart적용
        initSignatureChart(signatureChartMajor, signatureListMajor, true)
        initSignatureChart(signatureChartMinor, signatureListMinor, false)

        signatureChartMajor.invalidate()
        signatureChartMinor.invalidate()

        /** Button Listener **/


        return mainView
    }

    // Bar의 x축을 String으로 변환하는 인터페이스
    class AxisValueFormatter : com.github.mikephil.charting.formatter.IndexAxisValueFormatter {

        lateinit var mValues : Array<String>

        constructor(labels : Array<String>) {
            mValues = labels
        }

        override fun getFormattedValue(value: Float): String {
            return mValues[value.toInt()]
        }
    }

    // 피치 분포도 차트에 데이터 삽입
    private fun initPitchChart(pitchChart: PieChart, dataList : ArrayList<Int>) {
        if( dataList.size != 12) {
            throw IndexOutOfBoundsException("data size must be 12")
        }
        val keyList = arrayListOf<String>(
            "C", "Db","D","Eb"
            ,"E","F","Gb","G"
            ,"Ab","A","Bb","B"
        )
        val dataEntryList = ArrayList<PieEntry>()
        for(i in 0..11)
            dataEntryList.add(PieEntry(dataList[i].toFloat(), keyList[i]))
        val dataSet = PieDataSet(dataEntryList, "Pitch Pie Chart")

        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.colors = ColorTemplate.createColors(ColorTemplate.JOYFUL_COLORS)

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(10f)
        pitchChart.data = pieData
        pitchChart.holeRadius = 0f

        pitchChart.animateXY(1000, 1000)
    }

    // 키 시그니처 분포도 작성
    private fun initSignatureChart(signatureChart: BarChart, dataList : ArrayList<Int>, isMajor : Boolean) {
        if( dataList.size != 12) {
            throw IndexOutOfBoundsException("data size must be 12")
        }

        var title : String = ""
        val labelList : Array<String>
        // 라벨 설정
        if(isMajor) {
            labelList = arrayOf(
                "C Major", "Db Major", "D Major", "Eb Major", "E Major", "F Major",
                "Gb Major", "G Major", "Ab Major", "A Major", "Bb Major", "B Major"
            )
            title = "Major Signature"
        } else {
            labelList = arrayOf(
                "C Minor", "C# Minor", "D Minor", "Eb Minor", "E Minor", "F Minor",
                "Gb Minor", "G Minor", "G# Minor", "A Minor", "Bb Minor", "B Minor"
            )
            title = "Minor Signature"
        }

        val xAxis = signatureChart.xAxis
        xAxis.valueFormatter = StatisticFragment.AxisValueFormatter(labels = labelList)

        val charDataList = ArrayList<BarEntry>()

        for(i in 0..11) {
            charDataList.add(BarEntry(i.toFloat(), dataList[i].toFloat()))
        }
        val dataSet = BarDataSet(charDataList, title)

        dataSet.colors = ColorTemplate.createColors(ColorTemplate.JOYFUL_COLORS)

        val barData = BarData(dataSet)
        barData.setValueTextSize(10f)
        signatureChart.data = barData

        signatureChart.animateY(2000)

        /** 차트 터치이벤트 **/
        class ChartTouchListener : OnChartValueSelectedListener {
            override fun onNothingSelected() {

            }
            override fun onValueSelected(
                e: com.github.mikephil.charting.data.Entry?,
                h: Highlight?
            ) {
                if(e != null)
                    Toast.makeText(activity, labelList[e.x.toInt()], Toast.LENGTH_SHORT).show()
            }
        }
        signatureChart.setOnChartValueSelectedListener(ChartTouchListener())
    }
}
