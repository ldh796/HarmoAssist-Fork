package kr.sweetcase.harmoassist

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_technic_summary.*
import kr.sweetcase.harmoassist.modules.technicDictionary.TechnicalInfo
import org.billthefarmer.mididriver.MidiDriver
import java.io.IOException

class TechnicSummaryActivity : AppCompatActivity(), MidiDriver.OnMidiStartListener {

    lateinit var techInfo : TechnicalInfo

    lateinit var title : TextView
    lateinit var summary : TextView
    lateinit var spinner: Spinner
    var chordLevel = 0

    val octaveLevel = 4
    lateinit var activity : Context
    lateinit var midiDriver: MidiDriver

    // get String array from TechnicalInfo
    fun getStringArrayFromTechnicalInfo() : ArrayList<String> {

        val resultStrArr = ArrayList<String>()


        return resultStrArr
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_technic_summary)

        title = findViewById(R.id.tech_summary_title)
        summary = findViewById(R.id.tech_summary_text)
        spinner = findViewById(R.id.tech_test_spinner)
        activity = this
        // 데이터 전달받기
        techInfo = intent.getSerializableExtra("tech_info") as TechnicalInfo
        // TODO 재생용 미디데이터도 포함해야됨

        title.text = techInfo.technicName

        // 설명 세팅
        summary.text = techInfo.summary

        // 스피너 세팅
        val chordItems = resources.getStringArray(R.array.tech_chord_arr)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, chordItems)
        spinner.adapter = adapter

        // 스피너 리스너
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                // TODO 여기서 코드에 맞게 재생용 미디데이터 조정
                chordLevel = position
            }
        }

        midiDriver = MidiDriver()
        midiDriver.setOnMidiStartListener(this)
        midiDriver.start()


        // 화음 출력
        technic_chord_text.text = "V7sus4 -> V7 -> I"
        // 재생 버튼
        tech_play_btn.setOnClickListener {

            // 반복재생을 할수 없게 설정
            tech_play_btn.isEnabled = false

            // 재생
            for(idx in techInfo.technicMidiData.indices) {
                // 데이터 추출
                val chordArr = techInfo.technicMidiData[idx].chordArray
                val pitchArr = techInfo.technicMidiData[idx].pitchArray

                // pitch 재조정
                val pitchByteArr = ArrayList<Byte>()
                for(pidx in pitchArr.indices)
                    pitchByteArr.add((pitchArr[pidx].dec + chordLevel).toByte())

                // 화음 배열을 순차적으로 재생
                for(midx in chordArr.indices) {

                    // 여러음을 동시에 재생하려면 순차적으로 Sleep 없이 재생해야 한다.
                    for(cidx in chordArr[midx].indices) {
                        midiDriver.write(
                            byteArrayOf(
                                (0x90.or(0x00)).toByte(),
                                // 화음 중 단음 Pitch, 루트 Pitch + 선택한 Pitch, 3옥타브 위
                                (chordArr[midx][cidx] + pitchByteArr[midx] + (12 * octaveLevel)).toByte(),
                                0x7F
                            )
                        )
                    }
                    Thread.sleep(500)

                    // 다시 꺼야됨
                    for(cidx in chordArr[midx].indices) {
                        midiDriver.write(
                            byteArrayOf(
                                (0x90.or(0x00)).toByte(),
                                // 화음 중 단음 Pitch, 루트 Pitch + 선택한 Pitch, 3옥타브 위
                                (chordArr[midx][cidx] + pitchByteArr[midx] + (12 * octaveLevel)).toByte(),
                                0x00
                            )
                        )
                    }
                    Thread.sleep(100)
                }
            }
            // 다 끝났으므로 해제
            tech_play_btn.isEnabled = true
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        midiDriver.stop()
    }

    // 미디 재생
    override fun onMidiStart() {
    }
}
