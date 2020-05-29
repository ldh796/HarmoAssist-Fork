package kr.sweetcase.harmoassist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_custom_technic.*

class CustomTechnicActivity : AppCompatActivity() {

    //TODO 프로토타입용
    var selectedPitch = "C"
    var selectedChord = "Maj"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_technic)

        val chordItems = resources.getStringArray(R.array.tech_chord)
        val pitchItems = resources.getStringArray(R.array.tech_chord_arr)

        custom_tech_chord_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, chordItems)
        custom_tech_pitch_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, pitchItems)
        custom_tech_play_pitch_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, pitchItems)

        // 화음 데이터 갖고오기
        custom_tech_chord_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedChord = chordItems[position]
            }
        }

        // Pitch 데이터 갖고오기
        custom_tech_pitch_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedPitch = pitchItems[position]
            }
        }
        custom_tech_insert_btn.setOnClickListener {
            val bufString = custom_tech_result_text.text.toString()
            if(bufString.isEmpty())
                custom_tech_result_text.text = selectedPitch + selectedChord
            else
                custom_tech_result_text.text = bufString + " -> " + selectedPitch + selectedChord
        }

        //초기화 버튼
        custom_tech_restart_btn.setOnClickListener {
            custom_tech_result_text.text = ""
        }

    }
}
