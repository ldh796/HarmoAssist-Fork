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
 *  커서 추상 클래스: 위치를 가리키는 커서로 노트, 마디, 컨테이너 커서로 사용한다.
 *  @ param
 *      measureList : LinkedList<Measure>
 */

package kr.sweetcase.amclib.midiManager.midiController.cursor

abstract class CursorControl {

    private var cursor : Int = 0

    init {
        // 커서 초기화
        cursor = 0
    }

    fun getCursorIndex() : Int { return cursor }

    // 하위 클래스용 함수들 (절때 public으로 바꾸지 말것)
    protected val cursorIndex = {
        cursor
    }

    protected val lshift = {
        cursor--
    }
    protected val rshift = {
        cursor++
    }
    protected val setCursor = {
        newIdx : Int -> cursor = newIdx
    }

    fun getCursorIdx() : Int { return cursor }

    // 맨 앞인지 뒤인지 확인
    abstract fun isFront() : Boolean
    abstract fun isEnd() : Boolean

    // 맨 앞과 뒤로 이동
    abstract fun front() : Boolean
    abstract fun end() : Boolean

    // 다음 부분이 있는 확인
    abstract fun hasNext() : Boolean
    abstract fun hasBack() : Boolean

    // 다음 인덱스로 이동
    abstract fun move(targetIndex : Int) : Boolean

    // length 길이만큼 뛰어넘기
    abstract fun jump(length : Int) : Boolean

    // length  길이만큼 뒤로 뛰어넘기
    abstract fun jumpBack(length : Int) : Boolean

    /** 주의: jump와 jumpBack은 끝에 있을 경우만 false 처리하고
     *  length 길이 이전에 끝이 보이면 끝 까지 가고 true  처리
     **/

    // 다음 부분 이동
    abstract fun next() : Boolean
    abstract fun back() : Boolean

}