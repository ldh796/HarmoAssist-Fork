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

/**
 *  트랙 클래스: 여러 개의 마디를 가진 하나의 트랙
 *  @ param
 *      measureList : LinkedList<Measure>
 */
package kr.sweetcase.amclib.midiManager.midiController

import kr.sweetcase.amclib.midiManager.RangeOutException
import kr.sweetcase.amclib.midiManager.midiController.data.Measure
import kr.sweetcase.amclib.midiManager.midiController.data.TimeSignature
import java.util.*

open class MeasureList {

    private var measureList = LinkedList<Measure>()

    constructor()

    // 마디 갯수 구하기
    fun getMeasureLength() : Int { return measureList.size }

    // 마디 추가
    fun addMeasure(timeSignature: TimeSignature, tempo : Float) {
        if( (tempo <= 0f))
            throw RangeOutException("tempo must be over zero")
        measureList.add(Measure(timeSignature, tempo))
    }

    // 마디 데이터 불러오기
    fun getMeasure(index : Int): Measure {

        if( (index < 0).and(index >= measureList.size))
            throw RangeOutException("out of index")

        return measureList[index]
    }

    // 마디 삭제
    fun removeMeasure(index : Int) {

        if( (index < 0).and(index >= measureList.size))
            throw RangeOutException("out of index")
        measureList.removeAt(index)
    }
}