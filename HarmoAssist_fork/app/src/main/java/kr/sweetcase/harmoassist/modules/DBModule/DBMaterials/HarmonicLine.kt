package kr.sweetcase.harmoassist.modules.DBModule.DBMaterials

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kr.sweetcase.harmoassist.modules.DBModule.DBHandler

class HarmonicLine {

    // TODO 트랙 인덱스는 항상 1번으로 잡아놔야됨
    var chord:String=""
    var chordStyle:String=""
    var title:String=""
    var trackIndex:Int=1
    var MeasureIndex:Int=0


    //fun addHarmonicLine(db: SQLiteDatabase, harmonicLine: HarmonicLine, title: String){

    fun addHarmonicLine(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, chord: String, chordStyle: Int){
        //val handler=dBhandler
        //val db=this.writableDatabase
        /*val selectQuery=("SELECT MAX(${DBhandler.MEASURE_INDEX}) FROM ${DBhandler.FIRST_HAMONICLINE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var index: Int
        if (cursor.moveToFirst()){
            //index=cursor.getInt(cursor.getColumnIndex((DBhandler.MEASURE_INDEX)))
            index=cursor.getInt(0)
        }
        else{
            index=0
        }*/

        val values = ContentValues()

        values.put(DBHandler.TRACK_INDEX, trackIndex)
        values.put(DBHandler.MEASURE_INDEX, measureIndex)
        values.put(DBHandler.TITLE, title)
        values.put(DBHandler.CHORD, chord)
        values.put(DBHandler.CHORD_STYLE, chordStyle)

        db!!.insert(DBHandler.FIRST_HAMONICLINE_TABLE, null, values)
        //val values =("INSERT INTO VALUES "+DBhandler.FIRST_MEASURE_TABLE+" ("+measure.title+","+measure.trackIndex+","+measure.MeasureIndex)

        //db.execSQL(values)
        //val _success =db.insert(DBhandler.FIRST_MEASURE_TABLE, null, values)
        //db.close()

        //return (Integer.parseInt("$_success")==1)
        //return true
    }

    fun deleteAllHarmonicLineByTitle(delTitle: String, measureIndex: Int, db: SQLiteDatabase){
        db!!.delete(DBHandler.FIRST_HAMONICLINE_TABLE,"title=?", arrayOf(delTitle))
        //db!!.delete(DBhandler.FIRST_HAMONICLINE_TABLE,"title=? AND MeasureIndex=?", arrayOf(delTitle, measureIndex.toString()))
        //var sql=("DELETE FROM HarmonicLine WHERE title="+delTitle+"AND MeasureIndext="+measureIndex)
        //db.execSQL(sql)
    }

    fun delHarmonicLine(db: SQLiteDatabase, title: String, trackIndex: Int, deleteMeasureIndex: Int) {
        val selectQuery=("SELECT MAX(MeasureIndex) FROM ${DBHandler.FIRST_HAMONICLINE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var lastIndex: Int
        var startIndex: Int=deleteMeasureIndex
        var changeIndex=startIndex+1

        if (cursor.moveToFirst()){
            //index=cursor.getInt(cursor.getColumnIndex((DBhandler.MEASURE_INDEX)))
            lastIndex=cursor.getInt(0)
        }
        else{
            lastIndex=0
        }
        db!!.delete(DBHandler.FIRST_HAMONICLINE_TABLE,"Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=?", arrayOf(Integer.toString(deleteMeasureIndex)))

        while(startIndex<lastIndex) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_HAMONICLINE_TABLE} SET MeasureIndex=$startIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$changeIndex")
            db.execSQL(updateSql)
            startIndex++
            changeIndex++
        }
    }
    fun changeMeasureIndex(db: SQLiteDatabase, title: String, trackIndex: Int, changeMeasureIndex: Int, preMeasureIndex: Int){
        var updateSql =
            ("UPDATE ${DBHandler.FIRST_HAMONICLINE_TABLE} SET MeasureIndex=$changeMeasureIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$preMeasureIndex")
        db.execSQL(updateSql)
    }

    fun testdata(db: SQLiteDatabase){
        addHarmonicLine(db, "title1", 1,1, "c", 1)
        addHarmonicLine(db, "title1", 1,2, "c", 1)
        addHarmonicLine(db, "title1", 1,3, "c", 1)
        addHarmonicLine(db, "title1", 1,4, "c", 1)
        addHarmonicLine(db, "title1", 2,1, "c", 1)
        addHarmonicLine(db, "title1", 2,2, "c", 1)
        addHarmonicLine(db, "title1", 2,3, "c", 1)
        addHarmonicLine(db, "title1", 2,4, "c", 1)

        addHarmonicLine(db, "title2", 1,1, "c", 1)
        addHarmonicLine(db, "title2", 1,2, "c", 1)
        addHarmonicLine(db, "title2", 1,3, "c", 1)
        addHarmonicLine(db, "title2", 1,4, "c", 1)
        addHarmonicLine(db, "title2", 2,1, "c", 1)
        addHarmonicLine(db, "title2", 2,2, "c", 1)
        addHarmonicLine(db, "title2", 2,3, "c", 1)
        addHarmonicLine(db, "title2", 2,4, "c", 1)

    }

    fun updateHarmonicLine(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, chord: String, chordStyle: Int){
        val updateSql =
            ("UPDATE ${DBHandler.FIRST_HAMONICLINE_TABLE} SET Chord=\"${chord}\", ChordStyle=$chordStyle WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex")
        db.execSQL(updateSql)
        /*val values=ContentValues()
        values.put(DBhandler.TITLE, title)
        values.put(DBhandler.TRACK_INDEX, trackIndex)
        values.put(DBhandler.MEASURE_INDEX, measureIndex)
        values.put(DBhandler.CHORD_STYLE, chordStyle)
        values.put(DBhandler.CHORD, chord)

        db.update(DBhandler.FIRST_HAMONICLINE_TABLE, values, "Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$measureIndex",null)*/
    }
}