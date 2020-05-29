package kr.sweetcase.harmoassist.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.widget.Button
import android.widget.TextView
import kr.sweetcase.harmoassist.R

class SelectedMusicCommentDialog : Dialog {

    lateinit var commentText : TextView
    lateinit var okBtn : Button

    constructor(context: Context, comment : String, windowSize : Point) : super(context) {

        setContentView(R.layout.selected_music_comment_dialog)

        commentText = this.findViewById(R.id.selected_music_dialog_comment_text)
        okBtn = this.findViewById(R.id.comment_dialog_out_btn)

        commentText.text = comment
        okBtn.setOnClickListener {
            dismiss()
        }

        // 비율 조정
        window?.setLayout((windowSize.x * 0.5f).toInt(), this.window?.attributes!!.height)
    }
}