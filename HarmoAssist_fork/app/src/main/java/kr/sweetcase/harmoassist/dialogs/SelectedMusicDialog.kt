package kr.sweetcase.harmoassist.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import kr.sweetcase.harmoassist.*
import kr.sweetcase.harmoassist.listMaterials.Music
import kr.sweetcase.harmoassist.modules.DBModule.DBHandler

/**
 * 음약을 선택했을 때 뜨는 다이얼로그
 */
class SelectedMusicDialog : Dialog {

    private var activity : Activity

    private var music : Music
    private var titleText : TextView
    private var tempoText : TextView
    private var timeSignatureText : TextView
    private var chordText : TextView
    private var summaryText : TextView
    private var windowSize : Point

    private var summaryBtn : Button
    private var openBtn : Button
    private var closeBtn : Button
    private var statisticBtn : Button
    private var deleteBtn : Button

    private var shareImgBtn : ImageButton

    constructor(activity : Activity, music: Music, windowSize : Point) : super(activity) {
        this.music = music
        this.windowSize = windowSize
        this.activity = activity
        setContentView(R.layout.selected_list_dialog)

        titleText = this.findViewById(R.id.selected_music_title)
        tempoText = this.findViewById(R.id.selected_music_tempo)
        timeSignatureText = this.findViewById(R.id.selected_music_timesignature)
        chordText = this.findViewById(R.id.selected_music_chord)
        summaryText = this.findViewById(R.id.selected_music_comment)

        summaryBtn = this.findViewById(R.id.selected_music_comment_btn)
        openBtn = this.findViewById(R.id.selected_music_open_btn)
        closeBtn = this.findViewById(R.id.selected_music_close_btn)
        statisticBtn = this.findViewById(R.id.selected_music_statistic_btn)
        deleteBtn = this.findViewById(R.id.selected_music_delete_btn)

        shareImgBtn = this.findViewById(R.id.selected_music_share_btn)
    }
    // 시각화 데이터 세팅
    fun setDialog() {
        titleText.text = music.title
        tempoText.text = music.tempo.toString()
        timeSignatureText.text = music.timeSignature
        chordText.text = music.chord
        summaryText.text = music.summary
        this.window?.attributes!!.windowAnimations = R.style.ListSelectAnimation

        /* 화면 비율 조정 */
        window?.setLayout((windowSize.x * 0.8f).toInt(), (windowSize.y * 0.6f).toInt())
    }
    // 리스터 세팅
    fun setListener() {

        summaryBtn.setOnClickListener {

            if(music.summary != null) {
                // 다이얼로그 삽입
                val dialog = SelectedMusicCommentDialog(context, music.summary!!, windowSize)
                dialog.show()
            }
            else {
                Toast.makeText(this.context, "등록된 설명이 없습니다.", Toast.LENGTH_SHORT).show()
            }

        }

        // 열기
        openBtn.setOnClickListener {
            //intent 데이터 담기
            val enterIntent = Intent(this.activity, SheetRedirectionActivity::class.java)
            enterIntent.putExtra("type", MakeSheetType.CURRENT.key)
            enterIntent.putExtra(MakeSheetType.CURRENT.intentKeys[0], music)

            // 악보를 준비하는 리다이렉션 액티비티
            activity.startActivity(enterIntent)
            activity.overridePendingTransition(R.anim.rightin_activity_move, R.anim.rightout_activity_move)
            this.dismiss()
        }
        // 닫기
        closeBtn.setOnClickListener {
            this.dismiss()
        }
        // 분석
        statisticBtn.setOnClickListener {

            // TODO DB에 접속해 미디 데이터를 받고

            // TODO 악보 분석 페이지로 이동

            // 인텐트 준비
            val intent = Intent(context, StatisticActivity::class.java)
            // TODO DB로부터 받은 데이터를 인텐드에다 실어넣기
            // TODO 데이터 내용: 악보 정보와 미디데이터 구조체(Midi Control Library 필요)
            intent.putExtra("statistics_info", music)
            this.dismiss()
            context.startActivity(intent)
        }
        // 삭제
        deleteBtn.setOnClickListener {

            var isDelete = false

            // 다이얼로그 진입
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.deleted_music_layout)

            val yesBtn = dialog.findViewById<Button>(R.id.yes_delete_btn)
            val noBtn = dialog.findViewById<Button>(R.id.no_delete_btn)

            yesBtn.setOnClickListener {

                // TODO 삭제 버튼으로
                // TODO DB접근해서 항목 삭제
                val DBhander= DBHandler(context)

                var title=music.title
                DBhander.deleteFileInfo(title)

                val intent = Intent(context, MusicListActivity::class.java)

                dialog.dismiss()
                this.dismiss()
                context.startActivity(intent)

            }
            noBtn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

        }
        // 공유 버튼
        shareImgBtn.setOnClickListener {

            //TODO 실제로 미디파일을 전송해야 함
            var intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"

            // TODO 미디파일을 생성하는 코드를 작성
            intent.putExtra(Intent.EXTRA_SUBJECT, titleText.text.toString() + "mid") // TODO 실제로 미디파일 이름이 들어가야함
            intent.putExtra(Intent.EXTRA_TEXT, "extra text") // TODO 실재로 미디파일 내용이 들어가야함

            // 타이틀명 설정
            var chooser = Intent.createChooser(intent, titleText.text)
            this.context.startActivity(chooser)

        }
    }
}