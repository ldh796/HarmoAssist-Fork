package kr.sweetcase.harmoassist.modules.DBModule.DBMaterials

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kr.sweetcase.harmoassist.modules.DBModule.DBHandler

class FileInfo {
    var title:String=""
    var summary:String=""

    fun addFile(db: SQLiteDatabase, title: String, summary: String): Boolean{
        //val handler=dBhandler
        //val db=this.writableDatabase
        val values = ContentValues()

        values.put(DBHandler.TITLE, title)
        values.put(DBHandler.SUMMARY, summary)

        val _success =db.insert(DBHandler.FILEINFO_TABLE, null, values)
        //db.close()

        return (Integer.parseInt("$_success")==1)
        //return true
    }

    fun deleteFile(delTitle: String, db: SQLiteDatabase){

        /*db.delete("OneChord","title=?", arrayOf(delTitle))
        db.delete("Note","title=?", arrayOf(delTitle))
        db.delete("HarmonicLine","title=?", arrayOf(delTitle))
        db.delete("Measure","title=?", arrayOf(delTitle))
        db.delete("Sheet","title=?", arrayOf(delTitle))
        db.delete("FileInfo","title=?", arrayOf(delTitle))*/
        db!!.delete(DBHandler.FILEINFO_TABLE,"title=?", arrayOf(delTitle))
        /*var sql1:String=("DELETE FROM OneChord WHERE title="+delTitle)
        var sql2:String=("DELETE FROM Note WHERE title="+delTitle)
        var sql3:String=("DELETE FROM HarmonicLine WHERE title="+delTitle)
        var sql4:String=("DELETE FROM Measure WHERE title="+delTitle)
        var sql5:String=("DELETE FROM FileInfo WHERE title="+delTitle)
        db.execSQL(sql1)
        db.execSQL(sql2)
        db.execSQL(sql3)
        db.execSQL(sql4)
        db.execSQL(sql5)*/
    }

    fun testdata(db: SQLiteDatabase){
        addFile(db,"title1", "summary1")
        addFile(db,"title2", "summary1")
    }
}