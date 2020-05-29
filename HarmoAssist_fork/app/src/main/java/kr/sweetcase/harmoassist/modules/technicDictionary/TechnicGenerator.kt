package kr.sweetcase.harmoassist.modules.technicDictionary

import kr.sweetcase.amclib.midiManager.midiController.data.Pitch
import kr.sweetcase.harmoassist.modules.technicDictionary.labels.TechnicLabel

class TechnicGenerator {
    fun generate() : ArrayList<TechnicalInfo> {

        val technicList = ArrayList<TechnicalInfo>()

        technicList.add(
            TechnicalInfo(
                "도미넌트 베리에이션 I",
                arrayOf(TechnicalChord(
                    arrayOf(TechnicLabel._7_SUS_4.degreeArr, TechnicLabel.MAJ_7.degreeArr, TechnicLabel.MAJ.degreeArr),
                    arrayOf(Pitch.G, Pitch.G, Pitch.C)
                )),
                "곡의 종지를 효과적으로 만들 수 있는 도미넌트 베리에이션 중 하나이며 도미넌트 모션을 강조할 때 사용한다."
            )
        )
        technicList.add(
            TechnicalInfo(
                "세컨드리 도미넌트",
                arrayOf(TechnicalChord(
                    arrayOf(
                        TechnicLabel.MAJ_7.degreeArr,
                        byteArrayOf(
                            (TechnicLabel.MIN_7.degreeArr[0] + 12).toByte(),
                            (TechnicLabel.MIN_7.degreeArr[1] + 12).toByte(),
                            TechnicLabel.MIN_7.degreeArr[2],
                            TechnicLabel.MIN_7.degreeArr[3]
                        )
                    ),
                    arrayOf(Pitch.A, Pitch.D)
                )),
                "I 의외의 다이어토닉 코드를 임시 I로 보고 종지 느낌으로 강하게 접속할 때 사용한다."
            )
        )

        return technicList
    }
}