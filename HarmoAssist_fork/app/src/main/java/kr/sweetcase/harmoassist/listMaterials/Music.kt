package kr.sweetcase.harmoassist.listMaterials

import java.io.Serializable

/**
 * 음악 정보
 * titile : 제목
 * content : 설명
 * chord : 화음
 * tempo : 템포
 * timeSiganture : 박자표
 *
 * 이들은 차후에 백엔드를 포함시킬 때 타입을 다시 적용할 예정
 */
data class Music(
    val title: String,
    var summary:String?,
    val chord : String,
    val tempo : Int,
    val timeSignature : String) : Serializable {

    // 빌더패턴
    class Builder {
        private lateinit var title : String
        private var summary: String? = null
        private lateinit var chord : String
        private var tempo : Int = 0
        private lateinit var timeSignature : String

        fun getTitle(title : String) : Builder { this.title = title; return this }
        fun getSummary(summary : String?) : Builder { this.summary = summary; return this }
        fun getChord(chord : String) : Builder { this.chord = chord; return this }
        fun getTempo(tempo : Int) : Builder { this.tempo = tempo; return this }
        fun getTimeSignature(timeSignature : String ) : Builder { this.timeSignature = timeSignature; return this }

        fun build() : Music {
            return Music(title, summary, chord, tempo, timeSignature)
        }
    }

}