package kr.sweetcase.harmoassist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_music_info_from_sheet.*
import kr.sweetcase.harmoassist.listMaterials.Music

/** 악보 상에서 음악 정보 불러오기 **/
class MusicInfoFromSheetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_info_from_sheet)

        // 데이터 불러오기
        val musicInfo = intent.extras?.getSerializable("music_info") as Music

        // Setting
        music_title_from_sheet.text = musicInfo.title
        music_chord_from_sheet.text = musicInfo.chord
        music_timesignature_from_sheet.text = musicInfo.timeSignature
        music_tempo_from_sheet.text = musicInfo.tempo.toString()
        music_comment_from_sheet.text = musicInfo.summary

        back_btn_from_music_sheet.setOnClickListener {
            this.finish()
        }
    }
}
