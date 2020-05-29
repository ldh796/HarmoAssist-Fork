package kr.sweetcase.harmoassist.modules.DBModule.DBHandlers

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kr.sweetcase.harmoassist.modules.DBModule.DBHandler

class TitleDBHandler {
        var title=""
        var summary=""

    //DB에 파일 정보를 추가하는 함수
    fun addFile(db: SQLiteDatabase, title: String, summary: String){
        val values = ContentValues()

        values.put(DBHandler.TITLE, title)
        values.put(DBHandler.SUMMARY, summary)

        db.insert(DBHandler.FILEINFO_TABLE, null, values)
    }

    //DB에서 해당하는 파일 이름을 가진 정보들을 삭제하는 함수
    fun deleteFile(delTitle: String, db: SQLiteDatabase){
        db!!.delete(DBHandler.FILEINFO_TABLE,"title=?", arrayOf(delTitle))
    }

    fun changeFile(db: SQLiteDatabase, title: String, summary: String) {
        var updateSql =
            ("UPDATE ${DBHandler.FILEINFO_TABLE} SET Title=$title AND Summary=$summary WHERE Title=\"${title}\" AND Summary=\"${summary}\"")
        db.execSQL(updateSql)
    }

    fun testdata(db: SQLiteDatabase){
        addFile(db,"title1", "summary1")
        addFile(db,"title2", "summary1")
    }
}