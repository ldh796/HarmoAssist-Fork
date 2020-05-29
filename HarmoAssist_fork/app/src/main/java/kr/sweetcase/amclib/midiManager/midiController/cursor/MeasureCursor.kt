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

/**
 *  마디 커서 클래스 : 마디 리스트에서의 마디 위치
 *
 *  @ param
 *      measureList : MeasureList :  해당 마디 리스트 (1트랙)
 */

// 어차피 NoteCursor와 유사하니까 주석 생략
class MeasureCursor : CursorControl {

    private lateinit var measureList : MeasureList

    constructor(measureList: MeasureList) : super() {

        // 마디가 0개인 리스트일 경우
        if(measureList.getMeasureLength() == 0)
            throw IllegalAccessException("Number of Measure in MeasureList is Zero")

        this.measureList = measureList
    }

    // 다른 마디 리스트로 동기화
    fun changeMeasureList(measureList : MeasureList) {
        // 마디가 0개인 리스트일 경우
        if(measureList.getMeasureLength() == 0)
            throw IllegalAccessException("Number of Measure in MeasureList is Zero")

        this.measureList = measureList
        setCursor(0)
    }

    // 해당 커서에서 마디 데이터 갖고오기
    // 잘못된 데이터일 경우 NullPointerException이 발생할 수 있음
    fun getMeasure() : Measure {
        return this.measureList.getMeasure(cursorIndex())
    }


    // From Cursor Control Functions

    override fun isFront(): Boolean {
        return cursorIndex() == 0
    }
    override fun isEnd(): Boolean {
        return cursorIndex() == (this.measureList.getMeasureLength() - 1)
    }
    // 맨 앞으로 이동
    override fun front(): Boolean {

        if(isFront())
            return false
        setCursor(0)
        return true
    }
    // 맨 뒤로 이동
    override fun end(): Boolean {

        if(isEnd())
            return false
        setCursor(this.measureList.getMeasureLength() - 1)
        return true

    }
    override fun hasNext(): Boolean {
        return isEnd()
    }
    override fun hasBack(): Boolean {
        return isFront()
    }
    override fun move(targetIndex: Int): Boolean {

        return if( (targetIndex < 0).or(targetIndex >= this.measureList.getMeasureLength()))
            false
        else {
            setCursor(targetIndex)
            true
        }
    }

    override fun jump(length: Int): Boolean {
        return when {
            length < 0 -> throw RangeOutException("This Length Must Be over Zero")

            (cursorIndex() + length) >= this.measureList.getMeasureLength() -> false

            else -> {
                setCursor(cursorIndex() + length)
                true
            }
        }
    }

    override fun jumpBack(length: Int): Boolean {
        return when {
            length < 0 -> throw RangeOutException("This Length Must Be Over Zero")

            (cursorIndex() + length) < 0 -> false

            else -> {
                setCursor(cursorIndex() - length)
                true
            }
        }
    }

    override fun next(): Boolean {

        return if( isEnd() )
            false
        else {
            rshift()
            true
        }
    }

    override fun back(): Boolean {
        return if( isFront() )
            false
        else {
            lshift()
            true
        }
    }
}