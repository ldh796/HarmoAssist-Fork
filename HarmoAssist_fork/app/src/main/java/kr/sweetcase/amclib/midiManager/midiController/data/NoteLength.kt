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
package kr.sweetcase.amclib.midiManager.midiController.data
// 음 높이
/*
    @ param: length : 상대적인 길이 값
    온음표 부터 32분음 음표 까지 있으며 4분음표를 1로 기준으로 잡는다.
 */
enum class NoteLength(val length : Float) {
    L32_1(1/8f),
    L16_1(1/4f),
    L8_1(1/2f),
    L4_1(1f),
    L2_1(2f),
    L1_1(4f)
}