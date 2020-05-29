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

package kr.sweetcase.amclib.midiManager.midiController.cursor

import kr.sweetcase.amclib.midiManager.RangeOutException
import kr.sweetcase.amclib.midiManager.midiController.MeasureList
import kr.sweetcase.amclib.midiManager.midiController.data.Measure
import kr.sweetcase.amclib.midiManager.midiController.data.Note


/**
 *  노트 커서 클래스 : 해당 마디에서의 음표 및 쉼표 위치
 *
 *  @ param
 *      measure : Measure : 해당 마디
 */

class NoteCursor : CursorControl {

    private var measure: Measure

    constructor(measure: Measure) : super() {

        // 노트 갯수가 없으면 Error 처리
        if(measure.getNoteLength() == 0)
            throw IllegalAccessException("Number of Note in Measure is Zero")

        this.measure = measure
    }

    // 다른 마디로 동기화
    fun changeMeasure(measure: Measure) {

        // 노트 갯수가 없으면 Error 처리
        if(measure.getNoteLength() == 0)
            throw IllegalAccessException("Number of Note in Measure is Zero")

        this.measure = measure
        setCursor(0)

    }

    // 해당 커서에서 데이터 갖고오기
    fun getNote() : Note {
        return this.measure.getNote(getCursorIndex())
    }

    // from Cursor Control functions

    override fun isFront(): Boolean {
        return cursorIndex() == 0
    }

    override fun isEnd(): Boolean {
        return cursorIndex() == (this.measure.getNoteLength() - 1)
    }

    // 맨 앞으로 이동
    override fun front(): Boolean {
        // 이미 맨 앞이면 false
        if(isFront())
            return false
        
        setCursor(0)
        return true
    }

    // 맨 끝으로 이동
    override fun end(): Boolean {

        // 이미 맨 뒤면 false
        return if( isEnd() )
            false
        else  {
            setCursor(this.measure.getNoteLength() - 1)
            true
        }

    }

    // 바로 좌측 또는 우축에 데이터가 존재하는 경우
    override fun hasNext(): Boolean {
        return isEnd()
    }

    override fun hasBack(): Boolean {
        return isFront()
    }

    // 해당 인덱스로 이동
    override fun move(targetIndex: Int): Boolean {
        return if((targetIndex < 0).or(targetIndex >= this.measure.getNoteLength()))
            false
        else {
            setCursor(targetIndex)
            true
        }
    }

    override fun jump(length: Int): Boolean {
        return when {
            // 0 이하, 잘못된 입력
            length < 0 -> throw RangeOutException("This Length must be over 0")

            // 건너뛴 인덱스가 노트 갯수를 초과하였을 경
            (cursorIndex() + length) >= this.measure.getNoteLength() -> false

            // 맞음
            else -> {
                setCursor(cursorIndex() + length)
                true
            }
        }
    }

    override fun jumpBack(length: Int): Boolean {
        return when {
            length < 0 -> throw RangeOutException("This Length must be over 0")

            (cursorIndex() - length) < 0 -> false

            else -> {
                setCursor(cursorIndex() - length)
                true
            }
        }
    }

    // 다음 인덱스로 이동
    override fun next(): Boolean {
        return if( isEnd() )
            false
        else {
            rshift()
            true
        }
    }

    // 앞의 인덱스로 이동
    override fun back(): Boolean {
        return if( isFront() )
            false
        else {
            lshift()
            true
        }
    }

}