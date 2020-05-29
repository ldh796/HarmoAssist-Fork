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

import kr.sweetcase.amclib.midiManager.RangeOutException
import kr.sweetcase.amclib.midiManager.midiController.cursor.MeasureCursor
import kr.sweetcase.amclib.midiManager.midiController.cursor.NoteCursor
import kr.sweetcase.amclib.midiManager.midiController.data.Measure
import kr.sweetcase.amclib.midiManager.midiController.data.Note
import kr.sweetcase.amclib.midiManager.midiController.data.Pitch
import kr.sweetcase.amclib.midiManager.midiController.data.TimeSignature
import java.lang.NullPointerException

/**
 *   MidiController: 미디 데디터를 조작하는 최상위 클래스
 *
 *   TrackList : ArrayList<MeasureList>: 트랙 리스트
 *   trackCursor: 트랙 커서
 *   MeasureCursor: 미디 커서
 *   NoteCursor : 노트 커서
 *
 *   timeSignature : 박자(변경 불가능)
 *   chord : 코드(변경 불가능)
 *   isMajor : 장조 여부(true = 장조, false = 단조)(변경 불가능)
 *
 */

open class MidiController {

    private var trackList : ArrayList<MeasureList>
    private var trackCursor: Int = 0
    private var measureCursor: MeasureCursor
    private var noteCursor: NoteCursor

    private var timeSignature: TimeSignature
    private var chord : Pitch
    private var isMajor : Boolean = false
    private var tempo : Float  = 0f

    // 빌더를 쓰시는 게 안전합니다 ㅠㅠ
    protected constructor(

        trackSize : Int,
        measureSize : Int,
        timeSignature: TimeSignature,
        tempo : Float,
        chord : Pitch,
        isMajor : Boolean

    ) {
        /**
         *  @param:
         *      trackSize : 트랙 갯수
         *      measureSize : 비어있는 마디 갯수
         *      timeSignature: 박자
         *      tempo : 템포 박자 기준 bpm
         *      chord : Pitch
         *      isMajor : 장조 여부
         */

        if(trackSize < 1)
            throw RangeOutException("Track Size must be over 1")

        // 데이터 판정
        if(tempo <= 0f)
            throw RangeOutException("Tempo must be Over Zero")

        // 데이터 초기화
        trackList = ArrayList()
        this.tempo = tempo
        this.timeSignature = timeSignature
        this.isMajor = isMajor
        this.chord = chord

        // 트랙 채우기
        for(i in 0 until trackSize) {
            trackList.add(MeasureList())
            for( j in 0 until measureSize) {
                // 비어있는 마디 생성
                trackList[i].addMeasure(timeSignature, tempo)
            }
        }

        // 커서 초기화
        trackCursor = 0
        measureCursor = MeasureCursor(trackList[trackCursor])
        noteCursor = NoteCursor(trackList[trackCursor].getMeasure(0))
    }

    // Class Builder
    open class Builder {

        private var timeSignature: TimeSignature? = null
        private var chord : Pitch? = null
        private var isMajor : Boolean = false
        private var tempo : Float  = 0f

        private var trackSize : Int = 0
        private var measureSize : Int = 0

        fun setTimeSignature(timeSignature: TimeSignature) : Builder {
            this.timeSignature = timeSignature
            return this
        }
        fun setChord(chord : Pitch) : Builder {
            this.chord = chord
            return this
        }
        fun setIsMajor(isMajor: Boolean) : Builder {
            this.isMajor = isMajor
            return this
        }
        fun setTempo(tempo: Float) : Builder {
            this.tempo = tempo
            return this
        }
        fun setTrackSize(trackSize: Int) : Builder {
            this.trackSize = trackSize
            return this
        }
        fun setMeasureSize(measureSize: Int) : Builder {
            this.measureSize = measureSize
            return this
        }

        // 클래스 생성
        fun make() : MidiController {

            if( (timeSignature != null).or(chord != null) ) {

                try {
                    return MidiController(
                        trackSize,
                        measureSize,
                        timeSignature!!,
                        tempo,
                        chord!!,
                        isMajor
                    )
                } catch(ex : RangeOutException) {
                    throw ex
                }
            }
            else {
                throw NullPointerException("Check TimeSignature and chord")
            }
        }
    }

    // 커서 이동 함수들 (인덱스 지정)
    fun moveTrackCursor(targetIndex : Int) {
        if((targetIndex < 0).or(targetIndex >= trackList.size))
            throw RangeOutException("TrackList : Out Of Range")

        trackCursor = targetIndex

        // 커서 초기화
        measureCursor.changeMeasureList(trackList[trackCursor])
        noteCursor.changeMeasure(measureCursor.getMeasure())
    }
    fun moveMeasureCursor(targetIndex : Int) {
        if(!measureCursor.move(targetIndex))
            throw RangeOutException("MeasureCursor : Out of Range")

        noteCursor.changeMeasure(measureCursor.getMeasure())
    }
    fun moveNoteCursor(targetIndex: Int) {
        if(!noteCursor.move(targetIndex))
            throw RangeOutException("NoteCursor : Out of Range")
    }

    /***
     * 커서 인덱스 얻기
     * 1. 마디 커서
     * 2. 노트 커서
     */
    fun getMeasureCursorIdx() : Int { return measureCursor.getCursorIdx() }
    fun getNoteCursorIdx() : Int { return noteCursor.getCursorIdx() }

    /**
     *  커서 앞뒤로 가기
     *  1. 마디 커서
     *  2. 노트 커서
     */
    fun setMeasureCursorToNext() : Boolean {
        return if(measureCursor.next()) {
            noteCursor.changeMeasure(measureCursor.getMeasure())
            true
        } else {
            false
        }
    }
    fun setMeasureCursorToBack() : Boolean {
        return if(measureCursor.back()) {
            noteCursor.changeMeasure(measureCursor.getMeasure())
            true
        } else {
            false
        }
    }
    fun setNoteCursorToNext() : Boolean {

        // 끝에 다다른 경우, 마디를 한칸 앞으로 이동
        return if(!noteCursor.next()) {
            // 마디마저 끝에 다다르면 이동 불가능
            return setMeasureCursorToNext()
        } else {
            true
        }

    }
    fun setNoteCursorToBack() : Boolean {

        return if(!noteCursor.back()) {
            return setMeasureCursorToBack()
        } else {
            true
        }
    }

    /***
     *
     * 커서 맨 앞, 맨 뒤로 가기
     * 1. 마디 커서
     * 2. 노트 커서
     *
     */
    fun setMeasureCursorToFront() {
        measureCursor.front()
        noteCursor.changeMeasure(measureCursor.getMeasure())
    }
    fun setMeasureCursorToEnd() {
        measureCursor.end()
        noteCursor.changeMeasure(measureCursor.getMeasure())
    }
    fun setNoteCursorToFront() {
        noteCursor.front()
    }
    fun setNoteCursorToEnd() {
        noteCursor.end()
    }


    /**
     *  데이터 획득
     *  1. 노트 데이터
     *  2. 마디 데이터
     */
    // 해당 커서의 노트 데이터 얻기
    fun getNote() : Note {
        return noteCursor.getNote()
    }
    // 해당 커서의 마디 데이터 얻기
    fun getMeasure() : Measure {
        return measureCursor.getMeasure()
    }

    /** 트랙 관리 (커서 위치에서 조작)
     *  FUNCTIONS
     *  1. 트랙 추가
     *  2. 트랙 삭제
     */
    fun addTrack() {

        // 저장할 마디
        var newMeasure = MeasureList()

        // 현존하는 마디 갯수 만큼 삽입
        for(i in 0 until trackList[trackCursor].getMeasureLength())
            newMeasure.addMeasure(timeSignature, tempo)

        //트랙 추가
        trackList.add(trackCursor, newMeasure)

        // 커서 리셋
        measureCursor.changeMeasureList(trackList[trackCursor])
        noteCursor.changeMeasure(measureCursor.getMeasure())

    }
    // 커서 위치에 삭제
    // 0개 트랙을 만들 수 없음
    fun removeTrack() {

        if( trackList.size == 1 )
            throw IllegalAccessException("Controller has least 1 track")

        var targetIdx = trackCursor
        // 인덱스가 맨 끝부분일 경우

        /*** 커서 이동 ***/
        if(targetIdx == trackList.size - 1)
            trackCursor--

        /** 삭제 **/
        trackList.removeAt(targetIdx)

        measureCursor.changeMeasureList(trackList[trackCursor])
        noteCursor.changeMeasure(measureCursor.getMeasure())

    }

}