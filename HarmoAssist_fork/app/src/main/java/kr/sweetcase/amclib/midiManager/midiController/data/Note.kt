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
 *  악보의 음표 및 쉼표 클래스
 *  @ param
 *      duration : Float --> 음 길이
 *      octaveList : LinkedList<Int> : 옥타브 리스트 음표 하나에 음이 여러개 들어가므로 리스트로 선언
 *      pitchList : LinkedList<Pitch> :  음높이 리스트 --> octaveList와 같은 이유
 *      velocity : 음 세기,  범위는 0부터 127 까지
 *
 *   @ 쉼표가 되는 기준은 octaveList와, pitchList가 비어있어야 하고, velocity가 0이 되어야 한다.
 */
package kr.sweetcase.amclib.midiManager.midiController.data

import kr.sweetcase.amclib.midiManager.DataOverLappingException
import kr.sweetcase.amclib.midiManager.RangeOutException
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList

class Note {
    var duration : Float
        set(value) {

        // 범위 판정
        if((value <= 0).and(value > 4))
            throw RangeOutException("raw Duration must be between 1 and 4")
        field = value
    }

    private var octaveList : LinkedList<Int> = LinkedList()
    private var pitchList : LinkedList<Pitch> = LinkedList()

    private var velocity : Int
        set(value) {
            field = value
        }


    /**
     *  Exception class
     */
    // 생성자
    // 처음 초기화는 쉼표로 한다.
    constructor(rawDuration : Float) {
        /**
         *  rawDuration : 음 길이의 실제 Float 값
         *  velocity : 음 세기 값
         */
        // check variables
        if ((rawDuration <= 0).and(rawDuration > 4))
            throw RangeOutException("raw Duration must be between 1 and 4")
        this.duration = rawDuration
        this.velocity = 0
    }
    constructor(duration : NoteLength) {
        this.duration = duration.length
        this.velocity = 0
    }

    // 얕은 복사용
    constructor(targetNote : Note) {
        this.duration = targetNote.duration
        this.velocity = targetNote.velocity

        for(i in 0 until targetNote.octaveList.size) {
            this.octaveList.add(targetNote.octaveList[i])
            this.pitchList.add(targetNote.pitchList[i])
        }

    }

    fun getVelocity() : Int { return this.velocity }

    fun changeVelocity(value: Int) {
        // 음표일 경우
        if(this.isPlayNote()) {

            if( (value < 1).and(value > 128) )
                throw RangeOutException("velocity : 1 ~ 128")
            this.velocity = value

        } else { // 쉼표일 경우
            if(value != 0)
                throw NullPointerException("This Note is not PlayNote")
            this.velocity = value
        }
    }

    // 음의 갯수 구하기
    fun getChordLength() : Int {
        return this.pitchList.size
    }

    fun isPlayNote() : Boolean {
        /**
         *  음표인지 쉼표인지 판별하는 메소드
         *  판별하는 방법은 옥타브 리스트와 피치 리스트가 비어 있어야 하고
         *  velocity가 0이면 쉼표이고 그렇지 않으면 음표가 된다.
         */
        return this.pitchList.isEmpty().not()
    }

    /**
     *  Enum Class로 duration 수정하기
     *
     */
    fun setDurationByNoteLength(noteLength : NoteLength) {
        this.duration = noteLength.length
    }


    fun deleteAllChord() {
        // 쉼표로 전환하는 함수

        while(!this.octaveList.isEmpty())
            this.octaveList.removeAt(this.octaveList.size-1)
        while(!this.pitchList.isEmpty())
            this.pitchList.removeAt(this.pitchList.size-1)
        this.velocity = 0
    }

    // 음 데이터 삽입
    fun insertChord(newPitch : Pitch, octave : Int, velocity : Int=0) {

        // octave check
        if((octave > OctaveRange.MAX_OCTAVE.maxRange).or(octave < OctaveRange.MIN_OCTAVE.maxRange))
            throw RangeOutException("octave is out of range")

        // 음 데이터가 없는 쉼표일 경우
        if(!isPlayNote()) {
            // velocity가 0이면 안된다.
            if((velocity < 0).or(velocity > 127)) {
                throw RangeOutException("In Free Note. velocty must be 1 ~ 127")
            }
            // 데이터 삽입 하고 프로세스 종료
            this.octaveList.add(octave)
            this.pitchList.add(newPitch)
            this.velocity = velocity
            return

        } else {
            //음표인 경우 옥타브와 피치 둘다 중복되는거 검색
            var counter : Int = 0
            for(counter in 0 until this.octaveList.size) {
                if( (newPitch.dec == this.pitchList[counter].dec).
                        and(octave == this.octaveList[counter]))
                    throw DataOverLappingException("chord Overlapped")
            }

            // 없으면 새로 삽입 시도
            this.octaveList.add(octave)
            this.pitchList.add(newPitch)
            return

        }

    }
    // 음의 인덱스 값 찾기
    // 없으면 Null 처리
    fun getChordIndex(pitch: Pitch, octave: Int) : Int? {

        // octave 범위 체크
        if(!( (OctaveRange.MIN_OCTAVE.maxRange <= octave).
                and(octave <= OctaveRange.MAX_OCTAVE.maxRange)  )){

            throw RangeOutException("octave is 3 ~ -3")
        }

        // 데이터가 있는 지 검사
        var idx = 0
        for(idx in 0 until this.octaveList.size) {
            if((pitch == this.pitchList[idx]).and(octave == this.octaveList[idx]))
                return idx
        }
        return null
    }

    // 인덱스에 있는 데이터 삭제(getChordIdx를 반드시 사용)
    fun removeChordByIdx( idx : Int ) {

        if( (idx < 0).and(idx >= this.octaveList.size) )
            throw RangeOutException("Out Of Range")
        octaveList.removeAt(idx)
        pitchList.removeAt(idx)

        // 음이 없어서 쉼표로 전환된 경우
        if(octaveList.isEmpty())
            velocity = 0
    }

    fun getChordSize() : Int {
        return this.pitchList.size
    }
    fun getOctave(idx : Int) : Int {
        if( (idx < 0).or(idx >= this.octaveList.size))
            throw RangeOutException("Out of range")
        return this.octaveList[idx]
    }
    fun getPitch(idx : Int) : Pitch {
        if( (idx < 0).or(idx >= this.octaveList.size))
            throw RangeOutException("Out of range")
        return this.pitchList[idx]

    }
    fun getOctaveArray() : LinkedList<Int> {
        return this.octaveList
    }
    fun getPitchArray() : LinkedList<Pitch> {
        return this.pitchList
    }
}