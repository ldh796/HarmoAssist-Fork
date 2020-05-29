package kr.sweetcase.amclib.midiManager

import kr.sweetcase.amclib.midiManager.midiController.MidiController

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

/***
 *
 * 모든 미디  데이터를 총괄하여 관리하는 클래스
 */

class MidiManager {

    private lateinit var midiController : MidiController
    // private lateinit var midiPlayer

    constructor(midiController: MidiController) {
        /**
         * 1. MidiController : Builder를 사용하여 구현
         */
        this.midiController = midiController
        //this.midiPlayer = MidiPlayer(midiController)
    }

    fun getMidiController() : MidiController { return midiController }

    // 미디파일 스트림 생성
    //fun makeMidiFile() : MidiFile

    //fun play() : 해당 커서 위치에 미디 재생
    //fun stop() : 정지 (커서가 맨 처음으로 돌아감)
    //fun pause() : 일시정지

}