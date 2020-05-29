package kr.sweetcase.harmoassist.modules.DBModule.DBHandlers

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kr.sweetcase.harmoassist.modules.DBModule.DBHandler

//DB Note 테이블을 사용하기 위한 클래스
class NoteDBHandler {
        var tarckIndex=0
        var measrueIndex=0
        var duration: Int=0
        var noteStyle: Int=0

    fun addNewNote(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, duration: Int, noteStyle: Int){
        //val handler=dBhandler
        //val db=this.writableDatabase

        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var index: Int

        if (cursor.moveToFirst()){
            //index=cursor.getInt(cursor.getColumnIndex((DBhandler.MEASURE_INDEX)))
            index=cursor.getInt(0)
        }
        else{
            index=0
        }

        val values=ContentValues()

        values.put(DBHandler.TRACK_INDEX, trackIndex)
        values.put(DBHandler.MEASURE_INDEX, measureIndex)
        values.put(DBHandler.TITLE, title)
        values.put(DBHandler.DURATION, duration)
        values.put(DBHandler.NOTE_STYLE, noteStyle)
        values.put(DBHandler.NOTE_INDEX, index+1)

        db!!.insert(DBHandler.FIRST_NOTE_TABLE, null, values)
    }

    //원하는 위치에 새로운 노트를 추가하는 함수
    fun addNote(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, noteIndex: Int, duration: Int, noteStyle: Int){
        //val handler=dBhandler
        //val db=this.writableDatabase

        //해당 마디의 최대노트 인덱스 파악
        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var index: Int

        if (cursor.moveToFirst()){
            //index=cursor.getInt(cursor.getColumnIndex((DBhandler.MEASURE_INDEX)))
            index=cursor.getInt(0)
        }
        else{
            index=0
        }
        index=1

        var num=index-noteIndex+1
        var changeIndex=index+1
        var targetIndex=index

        //노트를 추가하려는 위치의 인덱스 확보를 위해서 기존 인덱스를 변경
        for(i in 1..num) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET NoteIndex=$changeIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$targetIndex")
            db.execSQL(updateSql)
            targetIndex--
            changeIndex--
        }

        val values=ContentValues()

        values.put(DBHandler.TRACK_INDEX, trackIndex)
        values.put(DBHandler.MEASURE_INDEX, measureIndex)
        values.put(DBHandler.TITLE, title)
        values.put(DBHandler.DURATION, duration)
        values.put(DBHandler.NOTE_STYLE, noteStyle)
        values.put(DBHandler.NOTE_INDEX, noteIndex)

        db!!.insert(DBHandler.FIRST_NOTE_TABLE, null, values)
        //val values =("INSERT INTO VALUES "+DBhandler.FIRST_MEASURE_TABLE+" ("+measure.title+","+measure.trackIndex+","+measure.MeasureIndex)

        //db.execSQL(values)
        //val _success =db.insert(DBhandler.FIRST_MEASURE_TABLE, null, values)
        //db.close()

        //return (Integer.parseInt("$_success")==1)
        //return true
    }

    //해당하는 title을 가진 정보를 note DB에서 삭제하는 함수
    fun deleteAllNoteByTitle(delTitle: String, db: SQLiteDatabase){
        db!!.delete(DBHandler.FIRST_NOTE_TABLE,"title=?", arrayOf(delTitle))
    }

    //해당하는 measure을 가진 정보를 note DB에서 삭제하는 함수
    fun deleteNoteByMeasure(db: SQLiteDatabase, delTitle: String, measureIndex: Int){
        db!!.delete(DBHandler.FIRST_NOTE_TABLE,"title=? AND MeasureIndex=$measureIndex", arrayOf(delTitle))
    }

    //하나의 노트를 DB에서 삭제하는 함수
    fun delNote(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, deleteNoteIndex: Int) {
        var lastIndex: Int
        var startIndex: Int=deleteNoteIndex
        var changeIndex=startIndex+1

        db!!.delete(DBHandler.FIRST_NOTE_TABLE,"Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=?", arrayOf(Integer.toString(deleteNoteIndex)))
        
        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}")
        val cursor=db.rawQuery(selectQuery,null)

        if (cursor.moveToFirst()){
            lastIndex=cursor.getInt(0)
        }
        else{
            lastIndex=0
        }

        //삭제한 노트인덱스가 비어있지 않도록 인덱스들을 수정
        while(startIndex<lastIndex) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET NoteIndex=$startIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$changeIndex")
            db!!.execSQL(updateSql)
            startIndex++
            changeIndex++
        }
    }

    //DB에서 note정보를 수정하는 함수
    fun changeNote(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, noteIndex: Int, duration: Int, noteStyle: Int) {

        var updateSql =
            ("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET Duration=$duration AND NoteStyle=$noteStyle WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$noteIndex")
        db.execSQL(updateSql)

    }

    //노트를 분할하는 함수
    fun seperateNote(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, seperateNoteIndex: Int, sperate: Int) {
        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var lastIndex: Int
        var startIndex: Int=seperateNoteIndex
        var changeIndex: Int

        var duration: Int
        var noteStyle: Int

        //분할하려는 노트의 정보를 수집
        val selectInfoQuery=("SELECT Duration FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex} AND NoteIndex=${seperateNoteIndex}")
        val selectInfoQuery2=("SELECT NoteStyle FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex} AND NoteIndex=${seperateNoteIndex}")

        val cursor2=db.rawQuery(selectInfoQuery, null)
        val cursor3=db.rawQuery(selectInfoQuery2, null)

        cursor2.moveToFirst()
        cursor3.moveToFirst()

        duration=cursor2.getInt(0)
        noteStyle=cursor3.getInt(0)

        var changeDuration=duration/sperate

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

        //분할후에 생기는 노트들을 저장하기위해 기존 인덱스를 변경
        for(i in 0..num) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET NoteIndex=$changeIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$lastIndex")
            db.execSQL(updateSql)
            lastIndex--
            changeIndex--
        }
        db.execSQL(("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET Duration=$changeDuration WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$lastIndex"))

        num=sperate-1

        //분할되는 노트들의 정보를 DB에 저장
        for(i in 0..num) {
            val values = ContentValues()
            values.put(DBHandler.TRACK_INDEX, trackIndex)
            values.put(DBHandler.MEASURE_INDEX, measureIndex)
            values.put(DBHandler.TITLE, title)
            values.put(DBHandler.DURATION, duration)
            values.put(DBHandler.NOTE_STYLE, noteStyle)
            values.put(DBHandler.NOTE_INDEX, startIndex)
            db!!.insert(DBHandler.FIRST_NOTE_TABLE, null, values)
            startIndex++
        }
    }
    
    //노트를 병합하는 함수
    fun mergeNote(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, mergeNoteIndex: Int, mergeNum: Int) {
        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var lastIndex: Int
        var startIndex: Int=mergeNoteIndex
        var changeIndex: Int
        var changeDuration: Int
        var durationb=startIndex+mergeNum-1  //합칠 듀레이션 범위
        var durationsubmit=0;

        val selectQuery2=("SELECT Duration FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex} AND NoteIndex BETWEEN $startIndex AND $durationb")
        val cursor2=db.rawQuery(selectQuery2,null)
        val onechord=OneChordDBHandler()

        if (cursor.moveToFirst()){
            lastIndex=cursor.getInt(0)
        }
        else{
            lastIndex=0
        }
        if(lastIndex==0)
            return

        //병합되는 노트들의 Duration을 모두 합해서 저장
        if(cursor2.moveToFirst()){
            do{
                var a=cursor2.getInt(0)
                durationsubmit+=a
            }while(cursor2.moveToNext())
            var updateSql=("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET Duration=$durationsubmit WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$startIndex")
            db!!.execSQL(updateSql)
        }

        var delTargetIndex=startIndex+1
        //병합되어 사라지는 노트들의 정보를 DB에서 삭제
        for(i in 1..mergeNum){
            delNote(db, title, trackIndex, measureIndex, delTargetIndex)
            delTargetIndex++
        }

        var num=lastIndex-startIndex-mergeNum+1 //인덱스를 변경해야 될 데이터 수
        changeIndex=startIndex+1
        var targetIndex=durationb+1

        //합쳐져서 비게된 인덱스들을 채우기위해 기존 인덱스를 변경
        for(i in 0..num) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET NoteIndex=$changeIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=4")
            db!!.execSQL(updateSql)
            targetIndex--
            changeIndex--
        }
    }

    fun changeMeasureIndex(db: SQLiteDatabase, title: String, trackIndex: Int, changeMeasureIndex: Int, preMeasureIndex: Int){
        val oneChord=OneChordDBHandler()
        oneChord.changeMeasureIndex(db, title, trackIndex, changeMeasureIndex, preMeasureIndex)
        var updateSql =
            ("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET MeasureIndex=$changeMeasureIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$preMeasureIndex")
        db.execSQL(updateSql)
    }

    fun testdata(db: SQLiteDatabase){
        addNewNote(db, "title1",1,1,2,2)
        addNewNote(db, "title1",1,1,2,2)
        addNewNote(db, "title1",1,1,2,2)
        addNewNote(db, "title1",1,1,2,2)
        addNewNote(db, "title1",1,2,2,2)
        addNewNote(db, "title1",1,2,2,2)
        addNewNote(db, "title1",1,2,2,2)
        addNewNote(db, "title1",1,2,2,2)
        addNewNote(db, "title1",1,3,2,2)
        addNewNote(db, "title1",1,3,2,2)
        addNewNote(db, "title1",1,3,2,2)
        addNewNote(db, "title1",1,3,2,2)
        addNewNote(db, "title1",1,4,2,2)
        addNewNote(db, "title1",1,4,2,2)
        addNewNote(db, "title1",1,4,2,2)
        addNewNote(db, "title1",1,4,2,2)

        addNewNote(db, "title1",2,1,2,2)
        addNewNote(db, "title1",2,1,2,2)
        addNewNote(db, "title1",2,1,2,2)
        addNewNote(db, "title1",2,1,2,2)
        addNewNote(db, "title1",2,2,2,2)
        addNewNote(db, "title1",2,2,2,2)
        addNewNote(db, "title1",2,2,2,2)
        addNewNote(db, "title1",2,2,2,2)
        addNewNote(db, "title1",2,3,2,2)
        addNewNote(db, "title1",2,3,2,2)
        addNewNote(db, "title1",2,3,2,2)
        addNewNote(db, "title1",2,3,2,2)
        addNewNote(db, "title1",2,4,2,2)
        addNewNote(db, "title1",2,4,2,2)
        addNewNote(db, "title1",2,4,2,2)
        addNewNote(db, "title1",2,4,2,2)

        addNewNote(db, "title2",1,1,2,2)
        addNewNote(db, "title2",1,1,2,2)
        addNewNote(db, "title2",1,1,2,2)
        addNewNote(db, "title2",1,1,2,2)
        addNewNote(db, "title2",1,2,2,2)
        addNewNote(db, "title2",1,2,2,2)
        addNewNote(db, "title2",1,2,2,2)
        addNewNote(db, "title2",1,2,2,2)
        addNewNote(db, "title2",1,3,2,2)
        addNewNote(db, "title2",1,3,2,2)
        addNewNote(db, "title2",1,3,2,2)
        addNewNote(db, "title2",1,3,2,2)
        addNewNote(db, "title2",1,4,2,2)
        addNewNote(db, "title2",1,4,2,2)
        addNewNote(db, "title2",1,4,2,2)
        addNewNote(db, "title2",1,4,2,2)

        addNewNote(db, "title2",2,1,2,2)
        addNewNote(db, "title2",2,1,2,2)
        addNewNote(db, "title2",2,1,2,2)
        addNewNote(db, "title2",2,1,2,2)
        addNewNote(db, "title2",2,2,2,2)
        addNewNote(db, "title2",2,2,2,2)
        addNewNote(db, "title2",2,2,2,2)
        addNewNote(db, "title2",2,2,2,2)
        addNewNote(db, "title2",2,3,2,2)
        addNewNote(db, "title2",2,3,2,2)
        addNewNote(db, "title2",2,3,2,2)
        addNewNote(db, "title2",2,3,2,2)
        addNewNote(db, "title2",2,4,2,2)
        addNewNote(db, "title2",2,4,2,2)
        addNewNote(db, "title2",2,4,2,2)
        addNewNote(db, "title2",2,4,2,2)

    }
}