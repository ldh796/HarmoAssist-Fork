package kr.sweetcase.harmoassist

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_combo.*
import kr.sweetcase.harmoassist.listMaterials.Music

/*
콤보 박스 구현 및 dialog 구현 부분
 */
class ComboActivity :AppCompatActivity(){

    private var aiSelected = false //ai 옵션 설정 여부
    // ai 옵션 리스트
    var selectedAIOptionList = arrayOf<String>(
        "CHOPIN",
        "BACH",
        "BEETHOVEN",
        "SCARLATTI",
        "BALAD",
        "NEW_AGE"
    )
    // 선택된 ai 인덱스
    private var aiSelectedPos = -1

    lateinit var newTitle : String
    lateinit var newChordString : String
    var newTempo : Int = 0
    var newEmptyMeasure : Int = 0
    lateinit var newTimeSignatureString : String
    var newComment : String? = null
    var hasComment = false

    // 데이터 유효성
    var completed = false

    // 스피너 리스너들

    // 1.AI 옵션 리스너
    class AIOptionSelectedListener(comboActivity: ComboActivity) : OnItemSelectedListener {
        var activity : ComboActivity = comboActivity
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            activity.aiSelectedPos = pos
        }
    }

    // 박자 옵션 리스너
    class TimeSignatureSelectedListener(comboActivity: ComboActivity) : OnItemSelectedListener {
        var activity = comboActivity

        override fun onNothingSelected(p0: AdapterView<*>?) {
        }
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            when(parent?.getItemAtPosition(pos).toString()) {
                "4분의 2박자" -> activity.newTimeSignatureString = "4_2"
                "4분의 3박자" -> activity.newTimeSignatureString = "4_3"
                "4분의 4박자" -> activity.newTimeSignatureString = "4_4"
                "8분의 3박자" -> activity.newTimeSignatureString = "8_3"
                "8분의 6박자" -> activity.newTimeSignatureString = "8_6"
            }
        }
    }

    // 코드 옵션 리스너
    class ChordSelectedListener(comboActivity: ComboActivity) : OnItemSelectedListener {
        var activity = comboActivity

        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            activity.newChordString = parent?.getItemAtPosition(pos).toString()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combo)
        //Spinner 구현(콤보박스)
        // 박자 콤보박스 구현
        val beatSpinner :Spinner = findViewById<Spinner>(R.id.spinner_beat) //박자 스피너 연결
        val beatAdapter = ArrayAdapter.createFromResource(this,
            R.array.beat,android.R.layout.simple_spinner_dropdown_item) //박자 스피너와 데이터 연결할 어댑터 정의
        //android.R.layout.simple_spinner_dropdown_item 사용 --> 아래로 스크롤 내려주며 아이템 보여줌
        beatSpinner.adapter = beatAdapter // 박자 스피너의 어댑터 설정
        beatSpinner.onItemSelectedListener = TimeSignatureSelectedListener(this)


        val chordSpinner = findViewById<Spinner>(R.id.spinner_chord) //코드 스피너 연결
        val chordAdapter = ArrayAdapter.createFromResource(this,
            R.array.chord,android.R.layout.simple_spinner_dropdown_item) //코드 스피너와 데이터 연결할 어댑터 정의
        chordSpinner.adapter = chordAdapter //코드 스피너의 어댑터 설정
        chordSpinner.onItemSelectedListener = ChordSelectedListener(this)

        // AI 옵션 설정
        val aiOptionSpinner = findViewById<Spinner>(R.id.ai_option_spinner)
        val aiOptionAdapter = ArrayAdapter.createFromResource(this,
            R.array.ai_option_arr, android.R.layout.simple_spinner_dropdown_item)
        aiOptionSpinner.adapter = aiOptionAdapter
        aiOptionSpinner.onItemSelectedListener = AIOptionSelectedListener(this)

        //버튼 눌렀을 경우의 구현부분
        CreateBtn.setOnClickListener{
            /*생성 버튼 눌렀을 경우 구현 부분 */

            // 1. 타이틀명 유효성 검토
            when {
                edit_name.text.toString().isEmpty() -> {

                    edit_name.hint = "이름을 입력하세요"
                    edit_name.setHintTextColor(ContextCompat.getColor(this, R.color.red))

                }
                edit_name.text.toString()[0] == ' ' -> {
                    edit_speed.text = null
                    edit_name.hint = "곡 이름은 띄어쓰기로 시작할 수 없습니다."
                    edit_name.setHintTextColor(ContextCompat.getColor(this, R.color.red))
                }
                else -> {
                    newTitle = edit_name.text.toString()
                }
            }

            // 2.템포 유효성 검토
            when {
                edit_speed.text.toString().isEmpty() -> {
                    edit_speed.hint = "빠르기를 입력하세요"
                    edit_speed.setHintTextColor(ContextCompat.getColor(this, R.color.red))
                }
                edit_speed.text.toString().toInt() > 300 -> {
                    edit_speed.text = null
                    edit_speed.hint = "템포는 300을 넘어설 수 없습니다."
                    edit_speed.setHintTextColor(ContextCompat.getColor(this, R.color.red))
                }
                edit_speed.text.toString().toInt() <= 0 -> {
                    edit_speed.text = null
                    edit_speed.hint = "템포는 0이 될 수 없습니다."
                    edit_speed.setHintTextColor(ContextCompat.getColor(this, R.color.red))
                }
                else -> {
                    newTempo = edit_speed.text.toString().toInt()
                }
            }

            // 반 악보 갯수 유효성 검토
            when {
                empty_measure_edit.text.toString().isEmpty() -> {
                    empty_measure_edit.hint = "빈 악보 갯수를 입력하세요"
                    empty_measure_edit.setHintTextColor(ContextCompat.getColor(this, R.color.red))
                }
                empty_measure_edit.text.toString().toInt() > 64 -> {
                    empty_measure_edit.text = null
                    empty_measure_edit.hint = "반 악보는 64개를 넘어설 수 없습니다."
                    empty_measure_edit.setHintTextColor(ContextCompat.getColor(this, R.color.red))
                }
                empty_measure_edit.text.toString().toInt() <= 0 -> {
                    empty_measure_edit.text = null
                    empty_measure_edit.hint = "빈 악보 갯수는 0개가 될 수 없습니다."
                    empty_measure_edit.setHintTextColor(ContextCompat.getColor(this, R.color.red))
                }
                else -> {
                    newEmptyMeasure = empty_measure_edit.text.toString().toInt()
                    completed = true
                }
            }

            //3. 노트사이즈 유효성 검토(AI 동작에 한에서만)
            if(aiSelected) {
                when {
                    note_size_text.text.toString().isEmpty() -> {
                        note_size_text.hint = "노트 갯수를 입력하세요"
                        note_size_text.setHintTextColor(ContextCompat.getColor(this, R.color.red))
                        completed = false
                    }
                    else -> completed = true
                }
            }


            // 나머지 두개는 콤보박스에서 자동 설정

            // 코멘트 여부
            if(edit_comment.text.toString().isNotEmpty()){
                hasComment = true
                newComment = edit_comment.text.toString()

            } else {
                hasComment = false
                newComment = null
            }

            if(completed) {
                // 데이터 생성
                val newMusic = Music.Builder()
                    .getChord(newChordString)
                    .getTempo(newTempo)
                    .getSummary(newComment)
                    .getTitle(newTitle)
                    .getTimeSignature(newTimeSignatureString)
                    .build()

                // TODO 로딩창 띄우기 위한 데이터 전송 준비
                val enterIntent = Intent(this, SheetRedirectionActivity::class.java)
                enterIntent.putExtra("new music data", newMusic)
                enterIntent.putExtra("empty measure", newEmptyMeasure)

                // TODO 얘는 테스트 용으로 악보가 추가될 시 악보 액티비티로 이동
                // AI작동 여부 세팅
                if(aiSelected) {
                    enterIntent.putExtra("ai option", selectedAIOptionList[aiSelectedPos])
                    enterIntent.putExtra("note size", note_size_text.text.toString().toInt())
                    enterIntent.putExtra("type", MakeSheetType.NEW_AI.key)
                } else {
                    enterIntent.putExtra("type", MakeSheetType.NEW.key)
                    Log.d("activity_test", "success0")
                }

                // TODO 악보 인터페이스로 이동하되
                // TODO AI를 선택한 경우 빈 악보로 생성하면 안됨
                startActivity(enterIntent)
                overridePendingTransition(R.anim.rightin_activity_move, R.anim.rightout_activity_move)
                this.finish()
            }
        }
        AI_RANDOM_Btn.setOnClickListener {
            if(aiSelected) {

                // AI 선택 해제
                aiSelected = false
                ai_option_layer.visibility = View.GONE
                it.setBackgroundColor(ContextCompat.getColor(this, R.color.real_gray))

                spinner_chord.isEnabled = true
                empty_measure_edit.isEnabled = true
            }
            else {

                // AI 선택
                aiSelected = true
                ai_option_layer.visibility = View.VISIBLE
                it.setBackgroundColor(ContextCompat.getColor(this, R.color.red))

                spinner_chord.isEnabled = false
                empty_measure_edit.isEnabled = false

            }
        }
    }
}