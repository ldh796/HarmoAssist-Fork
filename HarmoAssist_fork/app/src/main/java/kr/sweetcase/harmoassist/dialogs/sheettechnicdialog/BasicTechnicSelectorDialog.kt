package kr.sweetcase.harmoassist.dialogs.sheettechnicdialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.AdapterView
import com.google.android.material.button.MaterialButton
import kr.sweetcase.harmoassist.R

class BasicTechnicSelectorDialog : Dialog {

    //TODO 프로토타입용 화음 클래스
    enum class ChordLabel {
        MAJ, MIN, DIM, AUG, MAJ_MAJ_7, MAJ_7, MIN_7,
        MIN_7_FIFTH, DIM_7, _7_SUS_4, MAJ_6, MIN_6
    }

    var okBtn : MaterialButton //설정 버튼
    var cancelBtn : MaterialButton //취소 버튼

    var pitchToInt : Int = 0 // 피치
    //도가 0으로 시작하여 시가 11로 끝난다.
    //이 정수값을 이용해서 화음 음높이 조정 가능(예를들어 Major인데 피치가 D일 경우 C와 D는 2도 차이이고, D는 2의 값을 가지므로
    //기존 화음에 2도 씩 올리면 된다. (단, 솔(G)부터는 음이 너무 높이 올라가므로 한 옥타브 내린 다음 도수를 올린다.\

    var chordLabel : ChordLabel = ChordLabel.MAJ // 화음

    // 피치 옵션 리스너
    class PitchSelectListener(val dialog : BasicTechnicSelectorDialog) : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
            dialog.pitchToInt = pos
        }
    }
    class ChordSelectListener(val dialog : BasicTechnicSelectorDialog) : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            when(parent?.getItemAtPosition(pos).toString()) {
                "Maj" -> dialog.chordLabel = ChordLabel.MAJ
                "Min" -> dialog.chordLabel = ChordLabel.MIN
                "Dim" -> dialog.chordLabel = ChordLabel.DIM
                "Aug" -> dialog.chordLabel = ChordLabel.AUG
                "Maj-Maj-7" -> dialog.chordLabel = ChordLabel.MAJ_MAJ_7
                "Maj-7" -> dialog.chordLabel = ChordLabel.MAJ_7
                "Min-7" -> dialog.chordLabel = ChordLabel.MIN_7
                "Min-7(-5)" -> dialog.chordLabel = ChordLabel.MIN_7_FIFTH
                "dim-7" -> dialog.chordLabel = ChordLabel.DIM_7
                "7-sus-4" -> dialog.chordLabel = ChordLabel._7_SUS_4
                "Maj-6" -> dialog.chordLabel = ChordLabel.MAJ_6
                "Min-6" -> dialog.chordLabel = ChordLabel.MIN_6
            }
        }
    }

    // 화음 옵션 리스너
    constructor(context: Context) : super(context) {
        setContentView(R.layout.dialog_basic_technic_selector)

        okBtn = findViewById(R.id.basic_tech_ok_btn)
        cancelBtn = findViewById(R.id.basic_tech_cancel_btn)

        /** 설정 및 취소 버튼 ***/
        okBtn.setOnClickListener {
            // TODO 여기서 화음 세팅 (악보 데이터가 어떻게 구성이 될 지 몰라서 일부분 절치는 생략
            // TODO 너가 해야 할듯)
            // TODO chordLabel과 pitchToInt를 활용
            dismiss()
        }
        cancelBtn.setOnClickListener {
            dismiss()
        }
    }
}