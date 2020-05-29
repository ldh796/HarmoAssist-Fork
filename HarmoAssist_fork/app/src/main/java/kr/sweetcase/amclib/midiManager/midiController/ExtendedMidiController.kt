// Learn more or give us feedback
// Copyright SweetCase Project, Re_Coma(Ha Jeong Hyun). All Rights Reserved.
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//      http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package kr.sweetcase.amclib.midiManager.midiController

import kr.sweetcase.amclib.midiManager.midiController.data.Pitch
import kr.sweetcase.amclib.midiManager.midiController.data.TimeSignature

/***
 *
 * 확장형 미디 컨트롤러: 기존 미디컨트롤러의 확장 기능으로
 * 음표 추가 및 삭제 함수(Abstract)가 추가되었으며
 * 사용자의 알고리즘에 따라 직접 코딩을 해야 함
 *
 *  !Hint : Measure Class 에서의 노트 병합과 분할을 활용
 */
abstract class ExtendedMidiController : MidiController {

    // 빌더 클래스 사용 권장
    private constructor( trackSize : Int,
                        measureSize : Int,
                        timeSignature: TimeSignature,
                        tempo : Float,
                        chord : Pitch,
                        isMajor : Boolean
    ) : super (
        trackSize,
        measureSize,
        timeSignature,
        tempo,
        chord,
        isMajor
    )

    class Builder : MidiController.Builder()

    // 음표 및 쉼표 추가

    abstract fun addNote(
        /**
         * @param
         *      pitchArray 피치 리스트, 0이면 쉼표를 의미한다.
         *      octaveArray 피치와 매칭되는 옥타브 리스트, 갯수는 피치 리스트와 반드시 일치해야 한다.
         *      velocity : 소리 세기
         * @note
         *      삽입 위치는 커서 위치에서 수행
         */
        pitchArray : Array<Pitch>,
        octaveArray : Array<Int>,
        velocity : Float
    )
    abstract fun removeNote()
    /**
     *  @note
     *      삭제 위치는 커서에서 수행
     */


}