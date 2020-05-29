package kr.sweetcase.harmoassist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_technic_dictionary.*
import kr.sweetcase.amclib.midiManager.midiController.data.Pitch
import kr.sweetcase.harmoassist.listMaterials.TechnicAdapter
import kr.sweetcase.harmoassist.modules.technicDictionary.TechnicGenerator
import kr.sweetcase.harmoassist.modules.technicDictionary.TechnicalChord
import kr.sweetcase.harmoassist.modules.technicDictionary.TechnicalInfo
import kr.sweetcase.harmoassist.modules.technicDictionary.labels.TechnicLabel


class TechnicDictionaryActivity : AppCompatActivity() {


    lateinit var backBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_technic_dictionary)

        /** 컴포넌트 초기화 **/
        backBtn = findViewById(R.id.back_btn_from_tech_list)


        val techArray = TechnicGenerator().generate()

        /** 리사이클러뷰 세팅 **/
        val recyclerView = findViewById<RecyclerView>(R.id.technic_list_view)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = TechnicAdapter(techArray, this)
        recyclerView.adapter = adapter

        /** 컴포넌트 리스너
         * 그냥 창닫기 **/
        backBtn.setOnClickListener {
            onBackPressed()
        }
        custom_chord_btn.setOnClickListener {
            val intent = Intent(this, CustomTechnicActivity::class.java)
            startActivity(intent)
        }
    }
}
