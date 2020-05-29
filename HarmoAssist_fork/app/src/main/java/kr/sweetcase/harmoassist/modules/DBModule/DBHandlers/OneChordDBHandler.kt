package kr.sweetcase.harmoassist.modules.DBModule.DBHandlers

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kr.sweetcase.harmoassist.modules.DBModule.DBHandler

//DB OneChord 테이블을 사용하기 위한 클래스
class OneChordDBHandler {

    var trackIndex: Int=0
    var measureIndex: Int=0
    var pitch: String=""
    var noteIndex: Int=0
    var otave: Int=0
    
    fun addChord(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, pitch: String, noteIndex: Int, octave: Int){
        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var index: Int

        if (cursor.moveToFirst()){
            index=cursor.getInt(0)
        }
        else{
            index=0
        }
        index=1

        var num=index-noteIndex+1
        var changeIndex=index+1
        var targetIndex=index

        for(i in 1..num) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_ONECHORD_TABLE} SET NoteIndex=$changeIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$targetIndex")
            db.execSQL(updateSql)
            targetIndex--
            changeIndex--
        }

        val values=ContentValues()

        values.put(DBHandler.TRACK_INDEX, trackIndex)
        values.put(DBHandler.MEASURE_INDEX, measureIndex)
        values.put(DBHandler.TITLE, title)
        values.put(DBHandler.OCTAVE, octave)
        values.put(DBHandler.PITCH, pitch)
        values.put(DBHandler.NOTE_INDEX, noteIndex)

        db!!.insert(DBHandler.FIRST_ONECHORD_TABLE, null, values)
    }

    //DB에서 원하는 위치에 음 정보를 추가하는 함수
    fun addOneChord(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, pitch: String, noteIndex: Int, octave: Int){

        val values = ContentValues()

        values.put(DBHandler.TRACK_INDEX, trackIndex)
        values.put(DBHandler.MEASURE_INDEX, measureIndex)
        values.put(DBHandler.TITLE, title)
        values.put(DBHandler.OCTAVE, octave)
        values.put(DBHandler.PITCH, pitch)
        values.put(DBHandler.NOTE_INDEX, noteIndex)

        db!!.insert(DBHandler.FIRST_ONECHORD_TABLE, null, values)
    }

    //OneChord 테이블에서 해당하는 파일 이름을 가진 정보를 삭제하는 함수
    fun deleteAllOneChordByTitle(delTitle: String, db: SQLiteDatabase){
        db!!.delete(DBHandler.FIRST_ONECHORD_TABLE,"title=?", arrayOf(delTitle))
    }

    //OneChord 테이블에서 해당하는 마디의 음 정보를 모두 삭제하는 함수
    fun deleteOneChordByMeasure(db: SQLiteDatabase, delTitle: String, measureIndex: Int){
        db!!.delete(DBHandler.FIRST_ONECHORD_TABLE,"title=? AND MeasureIndex=$measureIndex", arrayOf(delTitle))
    }

    //OneChord 테이블에서 하나의 정보를 삭제하는 함수
    fun delOneChord(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, deleteNoteIndex: Int) {
        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_ONECHORD_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}") //수정을 원하는 노트가 있는 마디에서 가장큰 노트 인덱스를 불러오는 sql문
        val cursor=db.rawQuery(selectQuery,null)
        var lastIndex: Int  //가장 큰 노트인덱스를 저장하기 위한 변수
        var startIndex: Int=deleteNoteIndex //삭제를 원하는 음정의 인덱스를 저장하는 변수
        var changeIndex=startIndex+1    //삭제된 위치 이후에 있는 정보들의 인덱스를 변경하기 위한 변수

        //sql문으로 불러온 가장 큰 노트의 인덱스를 변수에 저장
        if (cursor.moveToFirst()){
            lastIndex=cursor.getInt(0)
        }
        else{
            lastIndex=0
        }
        //삭제를 원하는 음정의 정보를 삭제
        db!!.delete(DBHandler.FIRST_ONECHORD_TABLE,"Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=?", arrayOf(Integer.toString(deleteNoteIndex)))

        //삭제된 인덱스가 비어있지 않도록 노트 인덱스들을 수정
        while(startIndex<lastIndex) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_ONECHORD_TABLE} SET NoteIndex=$startIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$changeIndex")
            db.execSQL(updateSql)
            startIndex++
            changeIndex++
        }
    }

    //OneChord 테이블에서 마디 인덱스 정보를 수정하는 함수
    fun changeMeasureIndex(db: SQLiteDatabase, title: String, trackIndex: Int, changeMeasureIndex: Int, preMeasureIndex: Int){
        var updateSql =
            ("UPDATE ${DBHandler.FIRST_ONECHORD_TABLE} SET MeasureIndex=$changeMeasureIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$preMeasureIndex")
        db!!.execSQL(updateSql)
    }

    //OneChord 테이블에서 노트 인덱스를 수정하는 함수
    fun changeNoteIndex(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, changeNoteIndex: Int, preNoteIndex: Int){
        var updateSql =
            ("UPDATE ${DBHandler.FIRST_ONECHORD_TABLE} SET NoteIndex=$changeNoteIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$preNoteIndex")
        db.execSQL(updateSql)
    }

    //노트를 분할할 때 해당하는 음 정보동기화를 위한 함수
    fun seperateChord(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, seperateNoteIndex: Int, sperate: Int) {
        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var lastIndex: Int
        var startIndex: Int=seperateNoteIndex
        var changeIndex: Int

        var octave: Int
        var pitch: String

        val selectInfoQuery=("SELECT Pitch FROM ${DBHandler.FIRST_ONECHORD_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex} AND NoteIndex=${seperateNoteIndex}")
        val selectInfoQuery2=("SELECT Octave FROM ${DBHandler.FIRST_ONECHORD_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex} AND NoteIndex=${seperateNoteIndex}")

        val cursor2=db.rawQuery(selectInfoQuery, null)
        val cursor3=db.rawQuery(selectInfoQuery2, null)

        cursor2.moveToFirst()
        cursor3.moveToFirst()

        octave=cursor2.getInt(0)
        pitch=cursor3.getString(0)

        if (cursor.moveToFirst()){
            lastIndex=cursor.getInt(0)
        }
        else{
            lastIndex=0
        }
        if(lastIndex==0)
            return

        var num=lastIndex-startIndex-1
        changeIndex=lastIndex+sperate-1

        for(i in 0..num) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_ONECHORD_TABLE} SET NoteIndex=$changeIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$lastIndex")
            db.execSQL(updateSql)
            lastIndex--
            changeIndex--
        }

        num=sperate-1

        for(i in 0..num) {
            val values = ContentValues()
            values.put(DBHandler.TRACK_INDEX, trackIndex)
            values.put(DBHandler.MEASURE_INDEX, measureIndex)
            values.put(DBHandler.TITLE, title)
            values.put(DBHandler.PITCH, pitch)
            values.put(DBHandler.OCTAVE, octave)
            values.put(DBHandler.NOTE_INDEX, startIndex)
            db!!.insert(DBHandler.FIRST_ONECHORD_TABLE, null, values)
            startIndex++
        }
    }

    //노트를 병합할 때 해당하는 음 정보를 동기화하기 위한 함수
    fun mergeChord(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, mergeNoteIndex: Int, mergeNum: Int) {
        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var lastIndex: Int
        var startIndex: Int=mergeNoteIndex
        var changeIndex: Int
        var durationb=startIndex+mergeNum-1  //합칠 듀레이션 범위
        var durationsubmit=0;

        if (cursor.moveToFirst()){
            lastIndex=cursor.getInt(0)
        }
        else{
            lastIndex=0
        }
        if(lastIndex==0)
            return

        var delTargetIndex=startIndex+1
        for(i in 1..mergeNum){
            delOneChord(db, title, trackIndex, measureIndex, delTargetIndex)
            delTargetIndex++
        }

        var num=lastIndex-startIndex-mergeNum+1 //인덱스를 변경해야 될 데이터 수
        changeIndex=startIndex+1
        var targetIndex=durationb+1

        for(i in 0..num) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_ONECHORD_TABLE} SET NoteIndex=$changeIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=4")
            db!!.execSQL(updateSql)
            targetIndex--
            changeIndex--
        }
    }

    fun testdata(db: SQLiteDatabase){
        addOneChord(db, "title1", 1,1,"pitch1",1, 2)
        addOneChord(db, "title1", 1,1,"pitch1",2, 2)
        addOneChord(db, "title1", 1,1,"pitch3",3, 2)
        addOneChord(db, "title1", 1,1,"pitch3",4, 2)
        addOneChord(db, "title1", 1,2,"pitch4",1, 2)
        addOneChord(db, "title1", 1,2,"pitch5",2, 2)
        addOneChord(db, "title1", 1,2,"pitch6",3, 2)
        addOneChord(db, "title1", 1,2,"pitch1",4, 2)
        addOneChord(db, "title1", 1,3,"pitch2",1, 2)
        addOneChord(db, "title1", 1,3,"pitch6",2, 2)
        addOneChord(db, "title1", 1,3,"pitch6",3, 2)
        addOneChord(db, "title1", 1,3,"pitch7",4, 2)
        addOneChord(db, "title1", 1,4,"pitch7",1, 2)
        addOneChord(db, "title1", 1,4,"pitch8",2, 2)
        addOneChord(db, "title1", 1,4,"pitch6",3, 2)
        addOneChord(db, "title1", 1,4,"pitch5",4, 2)

        addOneChord(db, "title2", 1,1,"pitch2",1, 2)
        addOneChord(db, "title2", 1,1,"pitch3",2, 2)
        addOneChord(db, "title2", 1,1,"pitch2",3, 2)
        addOneChord(db, "title2", 1,1,"pitch2",4, 2)
        addOneChord(db, "title2", 1,2,"pitch3",1, 2)
        addOneChord(db, "title2", 1,2,"pitch2",2, 2)
        addOneChord(db, "title2", 1,2,"pitch1",3, 2)
        addOneChord(db, "title2", 1,2,"pitch5",4, 2)
        addOneChord(db, "title2", 1,3,"pitch5",1, 2)
        addOneChord(db, "title2", 1,3,"pitch6",2, 2)
        addOneChord(db, "title2", 1,3,"pitch8",3, 2)
        addOneChord(db, "title2", 1,3,"pitch9",4, 2)
        addOneChord(db, "title2", 1,4,"pitch9",1, 2)
        addOneChord(db, "title2", 1,4,"pitch9",2, 2)
        addOneChord(db, "title2", 1,4,"pitch4",3, 2)
        addOneChord(db, "title2", 1,4,"pitch2",4, 2)

        addOneChord(db, "title1", 1,1,"pitch2",1, 2)
        addOneChord(db, "title1", 1,1,"pitch2",2, 2)
        addOneChord(db, "title1", 1,1,"pitch2",3, 2)
        addOneChord(db, "title1", 1,1,"pitch8",4, 2)
        addOneChord(db, "title1", 1,2,"pitch8",1, 2)
        addOneChord(db, "title1", 1,2,"pitch9",2, 2)
        addOneChord(db, "title1", 1,2,"pitch8",3, 2)
        addOneChord(db, "title1", 1,2,"pitch9",4, 2)
        addOneChord(db, "title1", 1,3,"pitch9",1, 2)
        addOneChord(db, "title1", 1,3,"pitch6",2, 2)
        addOneChord(db, "title1", 1,3,"pitch7",3, 2)
        addOneChord(db, "title1", 1,3,"pitch6",4, 2)
        addOneChord(db, "title1", 1,4,"pitch6",1, 2)
        addOneChord(db, "title1", 1,4,"pitch5",2, 2)
        addOneChord(db, "title1", 1,4,"pitch3",3, 2)
        addOneChord(db, "title1", 1,4,"pitch2",4, 2)

        addOneChord(db, "title2", 1,1,"pitch",1, 2)
        addOneChord(db, "title2", 1,1,"pitch",2, 2)
        addOneChord(db, "title2", 1,1,"pitch",3, 2)
        addOneChord(db, "title2", 1,1,"pitch",4, 2)
        addOneChord(db, "title2", 1,2,"pitch",1, 2)
        addOneChord(db, "title2", 1,2,"pitch",2, 2)
        addOneChord(db, "title2", 1,2,"pitch",3, 2)
        addOneChord(db, "title2", 1,2,"pitch",4, 2)
        addOneChord(db, "title2", 1,3,"pitch",1, 2)
        addOneChord(db, "title2", 1,3,"pitch",2, 2)
        addOneChord(db, "title2", 1,3,"pitch",3, 2)
        addOneChord(db, "title2", 1,3,"pitch",4, 2)
        addOneChord(db, "title2", 1,4,"pitch",1, 2)
        addOneChord(db, "title2", 1,4,"pitch",2, 2)
        addOneChord(db, "title2", 1,4,"pitch",3, 2)
        addOneChord(db, "title2", 1,4,"pitch",4, 2)

    }
}