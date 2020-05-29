package kr.sweetcase.harmoassist.modules.DBModule.DBMaterials

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kr.sweetcase.harmoassist.modules.DBModule.DBHandler


class Measure {
    var title:String=""
    var trackIndex:Int=0
    var MeasureIndex:Int=0

    fun addMeasure(db: SQLiteDatabase, title: String, trackIndex: Int){
        //val handler=dBhandler
        //val db=this.writableDatabase
        val selectQuery=("SELECT MAX(MeasureIndex) FROM ${DBHandler.FIRST_MEASURE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var index: Int

        if (cursor.moveToFirst()){
            //index=cursor.getInt(cursor.getColumnIndex((DBhandler.MEASURE_INDEX)))
            index=cursor.getInt(0)
        }
        else{
            index=0
        }

        val values = ContentValues()

        values.put(DBHandler.TRACK_INDEX, trackIndex)
        values.put(DBHandler.MEASURE_INDEX, index+1)
        values.put(DBHandler.TITLE, title)

        db!!.insert(DBHandler.FIRST_MEASURE_TABLE, null, values)
        //val values =("INSERT INTO VALUES "+DBhandler.FIRST_MEASURE_TABLE+" ("+measure.title+","+measure.trackIndex+","+measure.MeasureIndex)

        //db.execSQL(values)
        //val _success =db.insert(DBhandler.FIRST_MEASURE_TABLE, null, values)
        //db.close()

        //return (Integer.parseInt("$_success")==1)
        //return true
    }

    fun deleteAllMeasureByTitle(delTitle: String, measureIndex: Int, trackIndext: Int, db: SQLiteDatabase){
        db!!.delete(DBHandler.FIRST_MEASURE_TABLE,"title=?", arrayOf(delTitle))
        //db!!.delete(DBhandler.FIRST_MEASURE_TABLE,"title=? AND MeasureIndex=?", arrayOf(delTitle, measureIndex.toString()))
        /*var sql=("DELETE FROM Measure WHERE title="+delTitle+"AND MeasureIndext="+measureIndex)
        db.execSQL(sql)*/
    }

    fun delMeasure(db: SQLiteDatabase, title: String, trackIndex: Int, deleteMeasureIndex: Int) {
        val selectQuery=("SELECT MAX(MeasureIndex) FROM ${DBHandler.FIRST_MEASURE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex}")
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
        db!!.delete(DBHandler.FIRST_MEASURE_TABLE,"Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=?", arrayOf(Integer.toString(deleteMeasureIndex)))

        while(startIndex<lastIndex) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_MEASURE_TABLE} SET MeasureIndex=$startIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$changeIndex")
            db.execSQL(updateSql)
            startIndex++
            changeIndex++
        }
    }

    fun testdata(db: SQLiteDatabase){
        addMeasure(db, "title1", 1)
        addMeasure(db, "title1", 1)
        addMeasure(db, "title1", 1)
        addMeasure(db, "title1", 1)
        addMeasure(db, "title1", 2)
        addMeasure(db, "title1", 2)
        addMeasure(db, "title1", 2)
        addMeasure(db, "title1", 2)

        addMeasure(db, "title2", 1)
        addMeasure(db, "title2", 1)
        addMeasure(db, "title2", 1)
        addMeasure(db, "title2", 1)
        addMeasure(db, "title2", 2)
        addMeasure(db, "title2", 2)
        addMeasure(db, "title2", 2)
        addMeasure(db, "title2", 2)

    }
}