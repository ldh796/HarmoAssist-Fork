// Learn more or give us feedback
// Copyright SweetCase Project, Re_Coma(Ha Jeong Hyun). All Rights Reserved.
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//      http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific lang

// 박자표
// @ param: totalLength: 해당 박자표가 요구하는 최대의 길이

package kr.sweetcase.amclib.midiManager.midiController.data

enum class TimeSignature(var TotalLength: Float) {
    T4_2(1 * 2f),
    T4_3(1 * 3f),
    T4_4(1 * 4f),
    T8_3(0.5f * 3),
    T8_6(0.5f * 6)
}