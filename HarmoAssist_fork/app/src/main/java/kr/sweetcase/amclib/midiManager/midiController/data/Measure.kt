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
 *  마디 클래스
 *  @ param
 *      noteList : Note List
 *      TimeSignature: 박자표
 *      tempo: 템포(4분음표 계열은 4분음표 기준 8분음표 계열은 8분음표 기준으로 정한다.)
 *      pedalMode: 페달의 여부로 재생을 할 때 마디가 끝날 때 까지 모든 음을 멈추지만 않으면 된다.
 */
package kr.sweetcase.amclib.midiManager.midiController.data

import kr.sweetcase.amclib.midiManager.RangeOutException
import java.util.*
import kotlin.collections.ArrayList

class Measure {

    private var noteList : LinkedList<Note>

    private var timeSignature: TimeSignature

    private var tempo : Float = 0f

    private var pedalMode : Boolean = false

    constructor(timeSignature: TimeSignature, tempo : Float, pedalMode : Boolean = false) {

        // check tempo
        if(tempo <= 0f)
            throw RangeOutException("Tempo must be over 0.0001")
        this.tempo = tempo
        this.timeSignature = timeSignature
        this.pedalMode = pedalMode

        this.noteList = LinkedList()

        // 전체 길이를 가진 쉼표 추가
        noteList.add(Note(this.timeSignature.TotalLength))
    }

    // 노트 갯수 구하기
    fun getNoteLength() : Int { return this.noteList.size }

    // 고정 데이터 수정
    fun setTimeSignature(timeSignature: TimeSignature) { this.timeSignature = timeSignature }
    fun setTempo(tempo : Float) {

        // check tempo
        if(tempo <= 0f)
            throw RangeOutException("Tempo must be over 0.0001")
        this.tempo = tempo
    }
    fun setPedalMode(pedalMode: Boolean) {
        this.pedalMode = pedalMode
    }

    // 노트 초기화
    fun reset() {
        this.noteList = LinkedList()
        this.noteList.add(Note(this.timeSignature.TotalLength))
    }

    // 노트 분할
    fun divideNote(startIdx : Int, time : Int) {

        if( (startIdx < 0).or(startIdx >= noteList.size) )
            throw RangeOutException("start index is out of range")
        if((time < 2))
            throw RangeOutException("time must be over 2")

        // change time
        var targetNote = noteList[startIdx]

        // 이전 노트 삭제
        targetNote.duration = targetNote.duration/time
        noteList.removeAt(startIdx)

        // 분할된 노트들 삽입
        for(i in 0 until time)
            noteList.add(startIdx, Note(targetNote))
    }

    // 노트 분할
    fun mergeNote(startIdx : Int, endIdx : Int) {

        // 타입 판정
        if( (startIdx < 0).and(startIdx >= endIdx) )
            throw RangeOutException("start index is out of range")
        if( (endIdx < 0).and(endIdx >= this.noteList.size) )
            throw RangeOutException("end index is out of range")

        // 병합 후의 길이
        var mergedLength = 0f

        //데이터는 처음 시작할 때의 노트 정보로 시작
        var targetNote = noteList[startIdx]

        for(idx in startIdx..endIdx) {
            mergedLength += noteList[startIdx].duration
            noteList.removeAt(startIdx)
        }
        // 길이 수정
        targetNote.duration = mergedLength

        // 수정된 데이터 삽입
        noteList.add(startIdx, Note(targetNote))
    }

    // 노트 데이터 불러오기
    fun getNote(index : Int) : Note {
        if( (index < 0).and(index >= this.noteList.size) )
            throw RangeOutException("index is out of range")

        return this.noteList[index]
    }
}