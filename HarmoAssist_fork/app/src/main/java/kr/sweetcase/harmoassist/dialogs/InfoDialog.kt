package kr.sweetcase.harmoassist.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.widget.Button
import android.widget.TextView
import kr.sweetcase.harmoassist.R

class InfoDialog : Dialog {


    lateinit var commentText : TextView
    lateinit var okBtn : Button

    constructor(context: Context, comment : String, windowSize : Point) : super(context) {

        setContentView(R.layout.info_dialog)
        okBtn = findViewById(R.id.info_ok_btn)
        commentText = findViewById(R.id.info_text)

        commentText.text = comment

        okBtn.setOnClickListener {
            dismiss()
        }
        // 비율 조정
        window?.setLayout((windowSize.x * 0.5f).toInt(), this.window?.attributes!!.height)
    }
}