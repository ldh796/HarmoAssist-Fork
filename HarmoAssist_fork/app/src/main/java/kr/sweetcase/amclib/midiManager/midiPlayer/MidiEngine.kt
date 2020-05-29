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

package kr.sweetcase.amclib.midiManager.midiPlayer

import kr.sweetcase.amclib.midiManager.midiController.data.Note
import org.billthefarmer.mididriver.MidiDriver
import java.lang.RuntimeException

/**
 *   MidiEngine : Leff.MidiDriver를 기반으로 한 클래스로 바이트 데이터를 낱개로 소리내는 클래스
 *
 *   midiDriver : 미디 드라이버(From com.leff.midi: MidiDriver 1.15.aar)
 *
 */


class MidiEngine : MidiDriver.OnMidiStartListener {

    // Midi Driver
    private var midiDriver: MidiDriver = MidiDriver()
    private var loaded : Boolean = false

    override fun onMidiStart() {
        loaded = true
    }

    init {
        midiDriver.setOnMidiStartListener(this)
    }

    // 미디 재생 준비
    fun load() {

        if(loaded)
            throw RuntimeException("Midi Driver is aleady loaded")

        midiDriver.start()

        if(!loaded)
            throw RuntimeException("MidiDriver Failed")
    }
    fun unload() {
        if(!loaded)
            throw RuntimeException("Midi Driver is aleady unloaded")

        midiDriver.stop()
        loaded = false
    }

    // 미디 재생
    fun playNote(note : Note) {

        if(note.isPlayNote()) {
            var event = ByteArray(3)
            event[0] = 0x90.toByte() // note On
            event[2] = note.getVelocity().toByte()

            // get pitch and octave
            val pitchArr = note.getPitchArray()
            val octaveArr = note.getOctaveArray()

            for(idx in 0 until pitchArr.size) {
                event[1] = (( 12 * (octaveArr[idx] + 5)) + pitchArr[idx].dec).toByte()
                midiDriver.write(event)
            }
        }
    }

    // 미디 재생 정지
    fun stopNote(note : Note) {
        if(note.isPlayNote()) {
            var event = ByteArray(3)
            event[0] = 0x80.toByte() // note Off
            event[2] = 0

            // get pitch and octave
            val pitchArr = note.getPitchArray()
            val octaveArr = note.getOctaveArray()

            for(idx in 0 until pitchArr.size) {
                event[1] = (( 12 * (octaveArr[idx] + 5)) + pitchArr[idx].dec).toByte()
                midiDriver.write(event)
            }
        }
    }
}