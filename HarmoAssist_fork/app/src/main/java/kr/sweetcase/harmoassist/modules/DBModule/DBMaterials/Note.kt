package kr.sweetcase.harmoassist.modules.DBModule.DBMaterials

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kr.sweetcase.harmoassist.modules.DBModule.DBHandler

class Note {

    /*fun addNote(db: SQLiteDatabase, note: Note, title: String){
        val values = ContentValues()
        values.put(DBhandler.NOTE_INDEX, note.noteIndex)
        values.put(DBhandler.NOTE_STYLE, note.noteStyle)
        values.put(DBhandler.DURATION, note.duration)
        values.put(DBhandler.TRACK_INDEX, note.trackIndex)
        values.put(DBhandler.MEASURE_INDEX, note.MeasureIndex)
        values.put(DBhandler.TITLE, note.title)
        db.insert(DBhandler.FIRST_NOTE_TABLE, null, values)
    }*/
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

        val values= ContentValues()

        values.put(DBHandler.TRACK_INDEX, trackIndex)
        values.put(DBHandler.MEASURE_INDEX, measureIndex)
        values.put(DBHandler.TITLE, title)
        values.put(DBHandler.DURATION, duration)
        values.put(DBHandler.NOTE_STYLE, noteStyle)
        values.put(DBHandler.NOTE_INDEX, index+1)

        db!!.insert(DBHandler.FIRST_NOTE_TABLE, null, values)
        //val values =("INSERT INTO VALUES "+DBhandler.FIRST_MEASURE_TABLE+" ("+measure.title+","+measure.trackIndex+","+measure.MeasureIndex)

        //db.execSQL(values)
        //val _success =db.insert(DBhandler.FIRST_MEASURE_TABLE, null, values)
        //db.close()

        //return (Integer.parseInt("$_success")==1)
        //return true
    }
    fun addNote(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, noteIndex: Int, duration: Int, noteStyle: Int){
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
        index=1

        var num=index-noteIndex+1
        var changeIndex=index+1
        var targetIndex=index

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

    fun deleteAllNoteByTitle(delTitle: String, measureIndex: Int, trackIndex: Int, noteIndex: Int, db: SQLiteDatabase){
        db!!.delete(DBHandler.FIRST_NOTE_TABLE,"title=?", arrayOf(delTitle))
        //db!!.delete(DBhandler.FIRST_NOTE_TABLE,"title=? AND MeasureIndex=?", arrayOf(delTitle, measureIndex.toString()))
        //var sql=("DELETE FROM Note WHERE title="+delTitle+" AND MeasureIndex="+measureIndex+" AND TrackIndext="+trackIndex+" AND NoteIndext="+noteIndex)
        //db.execSQL(sql)
    }

    fun delNote(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, deleteNoteIndex: Int) {
        var lastIndex: Int
        var startIndex: Int=deleteNoteIndex
        var changeIndex=startIndex+1

        db!!.delete(DBHandler.FIRST_NOTE_TABLE,"Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=?", arrayOf(Integer.toString(deleteNoteIndex)))

        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}")
        val cursor=db.rawQuery(selectQuery,null)

        if (cursor.moveToFirst()){
            //index=cursor.getInt(cursor.getColumnIndex((DBhandler.MEASURE_INDEX)))
            lastIndex=cursor.getInt(0)
        }
        else{
            lastIndex=0
        }

        while(startIndex<lastIndex) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET NoteIndex=$startIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$changeIndex")
            db!!.execSQL(updateSql)
            startIndex++
            changeIndex++
        }
    }

    fun changeNote(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, noteIndex: Int, duration: Int, noteStyle: Int) {

        var updateSql =
            ("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET Duration=$duration AND NoteStyle=$noteStyle WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$noteIndex")
        db.execSQL(updateSql)

    }

    fun seperateNote(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, mergeNoteIndex: Int, merge: Int) {
        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var lastIndex: Int
        var startIndex: Int=mergeNoteIndex
        var changeIndex: Int

        var duration: Int
        var noteStyle: Int

        val selectInfoQuery=("SELECT Duration FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex} AND NoteIndex=${mergeNoteIndex}")
        val selectInfoQuery2=("SELECT NoteStyle FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex} AND NoteIndex=${mergeNoteIndex}")

        val cursor2=db.rawQuery(selectInfoQuery, null)
        val cursor3=db.rawQuery(selectInfoQuery2, null)

        cursor2.moveToFirst()
        cursor3.moveToFirst()

        duration=cursor2.getInt(0)
        noteStyle=cursor3.getInt(0)

        var changeDuration=duration/merge

        if (cursor.moveToFirst()){
            //index=cursor.getInt(cursor.getColumnIndex((DBhandler.MEASURE_INDEX)))
            lastIndex=cursor.getInt(0)
        }
        else{
            lastIndex=0
        }
        if(lastIndex==0)
            return

        var num=lastIndex-startIndex-1
        changeIndex=lastIndex+merge-1
        //db!!.delete(DBhandler.FIRST_NOTE_TABLE,"Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=?", arrayOf(Integer.toString(deleteNoteIndex)))

        for(i in 0..num) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET NoteIndex=$changeIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$lastIndex")
            db.execSQL(updateSql)
            lastIndex--
            changeIndex--
        }
        db.execSQL(("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET Duration=$changeDuration WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$lastIndex"))
        seperate(db, title, trackIndex, measureIndex, mergeNoteIndex+1, changeDuration, noteStyle, merge)

    }
    fun seperate(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, startIndex: Int, duration: Int, noteStyle: Int, merge: Int) {
        var num=merge-1
        var index=startIndex
        for(i in 0..num) {
            val values = ContentValues()
            values.put(DBHandler.TRACK_INDEX, trackIndex)
            values.put(DBHandler.MEASURE_INDEX, measureIndex)
            values.put(DBHandler.TITLE, title)
            values.put(DBHandler.DURATION, duration)
            values.put(DBHandler.NOTE_STYLE, noteStyle)
            values.put(DBHandler.NOTE_INDEX, index)
            db!!.insert(DBHandler.FIRST_NOTE_TABLE, null, values)
            index++
        }
    }

    fun mergeNote(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, subNoteIndex: Int, mergeNum: Int) {
        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var lastIndex: Int
        var startIndex: Int=subNoteIndex
        var changeIndex: Int
        var changeDuration: Int
        var durationb=startIndex+mergeNum-1  //합칠 듀레이션 범위
        var durationsubmit=0;

        val selectQuery2=("SELECT Duration FROM ${DBHandler.FIRST_NOTE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex} AND NoteIndex BETWEEN $startIndex AND $durationb")
        val cursor2=db.rawQuery(selectQuery2,null)
        val onechord=OneChord()

        if (cursor.moveToFirst()){
            //index=cursor.getInt(cursor.getColumnIndex((DBhandler.MEASURE_INDEX)))
            lastIndex=cursor.getInt(0)
        }
        else{
            lastIndex=0
        }
        if(lastIndex==0)
            return

        if(cursor2.moveToFirst()){
            do{
                //var i=0
                var a=cursor2.getInt(0)
                durationsubmit+=a
                //i++
            }while(cursor2.moveToNext())
            var updateSql=("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET Duration=$durationsubmit WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$startIndex")
            db!!.execSQL(updateSql)
        }

        var delTargetIndex=startIndex+1
        for(i in 1..mergeNum){
            delNote(db, title, trackIndex, measureIndex, delTargetIndex)
            delTargetIndex++
        }

        var num=lastIndex-startIndex-mergeNum+1 //인덱스를 변경해야 될 데이터 수
        changeIndex=startIndex+1
        var targetIndex=durationb+1
        //db!!.delete(DBhandler.FIRST_NOTE_TABLE,"Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=?", arrayOf(Integer.toString(deleteNoteIndex)))

        for(i in 0..num) {
            //onechord.changeNoteIndex(db, title, trackIndex, measureIndex, changeIndex, targetIndex)
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_NOTE_TABLE} SET NoteIndex=$changeIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=4")
            db!!.execSQL(updateSql)
            targetIndex--
            changeIndex--
        }
    }

    fun changeMeasureIndex(db: SQLiteDatabase, title: String, trackIndex: Int, changeMeasureIndex: Int, preMeasureIndex: Int){
        val oneChord=OneChord()
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