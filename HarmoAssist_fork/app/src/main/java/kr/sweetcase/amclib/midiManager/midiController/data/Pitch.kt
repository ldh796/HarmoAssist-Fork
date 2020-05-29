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
    @ param: dec : 해당 피치의 정수값, str: 비주얼(문자열) 값
 */
enum class Pitch(val dec : Int, val text : String) {

    C_FLAT(11, "Cb"),
    C(0, "C"),
    C_SHARP(1, "C#"),

    D_FLAT(1, "Db"),
    D(2, "D"),
    D_SHARP(3, "D#"),

    E_FLAT(3, "Eb"),
    E(4, "E"),
    E_SHARP(5, "E#"),

    F_FLAT(4, "Fb"),
    F(5, "F"),
    F_SHARP(6, "F#"),

    G_FLAT(6, "Gb"),
    G(7, "G"),
    G_SHARP(8, "G#"),

    A_FLAT(8, "Ab"),
    A(9, "A"),
    A_SHARP(10, "A#"),

    B_FLAT(10, "Bb"),
    B(11, "B"),
    B_SHARP(0, "B#")

}