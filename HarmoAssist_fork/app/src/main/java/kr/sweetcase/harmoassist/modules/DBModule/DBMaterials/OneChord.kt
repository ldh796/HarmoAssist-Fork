package kr.sweetcase.harmoassist.modules.DBModule.DBMaterials
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kr.sweetcase.harmoassist.modules.DBModule.DBHandler

class OneChord {
    var pitch:String=""
    var noteIndex:Int=0
    var octave:Int=0
    var title:String=""
    var trackIndex:Int=0
    var MeasureIndex:Int=0

    /*fun addOneChord(db: SQLiteDatabase, oneChord: OneChord, title: String){
        val values = ContentValues()
        values.put(DBhandler.PITCH, oneChord.noteIndex)
        values.put(DBhandler.NOTE_INDEX, oneChord.noteIndex)
        values.put(DBhandler.OCTAVE, oneChord.octave)
        values.put(DBhandler.TRACK_INDEX, oneChord.trackIndex)
        values.put(DBhandler.MEASURE_INDEX, oneChord.MeasureIndex)
        values.put(DBhandler.TITLE, oneChord.title)
        db.insert(DBhandler.FIRST_ONECHORD_TABLE, null, values)
    }*/
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

    fun deleteAllOneChordByTitle(delTitle: String, measureIndex: Int, trackIndex: Int, noteIndex: Int, db: SQLiteDatabase){
        db!!.delete(DBHandler.FIRST_ONECHORD_TABLE,"title=?", arrayOf(delTitle))
        //db!!.delete(DBhandler.FIRST_ONECHORD_TABLE,"title=? AND MeasureIndex=?", arrayOf(delTitle, measureIndex.toString()))
        //var sql=("DELETE FROM OneChord WHERE title="+delTitle+" AND MeasureIndex="+measureIndex+" AND TrackIndext="+trackIndex+" AND NoteIndext="+noteIndex)
        //db.execSQL(sql)
    }

    fun delOneChord(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, deleteNoteIndex: Int) {
        val selectQuery=("SELECT MAX(NoteIndex) FROM ${DBHandler.FIRST_ONECHORD_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=${measureIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var lastIndex: Int
        var startIndex: Int=deleteNoteIndex
        var changeIndex=startIndex+1

        if (cursor.moveToFirst()){
            //index=cursor.getInt(cursor.getColumnIndex((DBhandler.MEASURE_INDEX)))
            lastIndex=cursor.getInt(0)
        }
        else{
            lastIndex=0
        }
        db!!.delete(DBHandler.FIRST_ONECHORD_TABLE,"Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=?", arrayOf(Integer.toString(deleteNoteIndex)))

        while(startIndex<lastIndex) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_ONECHORD_TABLE} SET NoteIndex=$startIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$changeIndex")
            db.execSQL(updateSql)
            startIndex++
            changeIndex++
        }
    }

    fun changeMeasureIndex(db: SQLiteDatabase, title: String, trackIndex: Int, changeMeasureIndex: Int, preMeasureIndex: Int){
        var updateSql =
            ("UPDATE ${DBHandler.FIRST_ONECHORD_TABLE} SET MeasureIndex=$changeMeasureIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$preMeasureIndex")
        db.execSQL(updateSql)
    }

    fun changeNoteIndex(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, changeNoteIndex: Int, preNoteIndex: Int){
        var updateSql =
            ("UPDATE ${DBHandler.FIRST_ONECHORD_TABLE} SET NoteIndex=$changeNoteIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex AND NoteIndex=$preNoteIndex")
        db.execSQL(updateSql)
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