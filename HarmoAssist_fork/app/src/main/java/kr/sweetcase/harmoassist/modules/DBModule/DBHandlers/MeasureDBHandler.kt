package kr.sweetcase.harmoassist.modules.DBModule.DBHandlers

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kr.sweetcase.harmoassist.modules.DBModule.DBHandler

//DB Measure 테이블을 사용하기 위한 클래스
class MeasureDBHandler {

    var trackIndex=0

    //DB에 Measure 정보를 추가하는 함수
    fun addMeasure(db: SQLiteDatabase, title: String, trackIndex: Int){
        val selectQuery=("SELECT MAX(MeasureIndex) FROM ${DBHandler.FIRST_MEASURE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex}")    //새로 추가할 마디의 인덱스를 알기 위해서 마지막 마디 인덱스를 불러오기 위한 sdql문
        val cursor=db.rawQuery(selectQuery,null)
        var index: Int  //마지막 마디의 인덱스를 저장하기 위한 함수

        //sql문으로 불러온 마지막 인덱스를 저장
        if (cursor.moveToFirst()){
            index=cursor.getInt(0)
        }
        else{
            index=0
        }

        val values = ContentValues()

        values.put(DBHandler.TRACK_INDEX, trackIndex)
        values.put(DBHandler.MEASURE_INDEX, index+1)    //마지막 인덱스 이후에 추가를 하기 때문에 +1을 해줌
        values.put(DBHandler.TITLE, title)

        db!!.insert(DBHandler.FIRST_MEASURE_TABLE, null, values)
    }

    //Measure 테이블에서 해당하는 파일 이름을 가진 정보를 삭제하는 함수
    fun deleteAllMeasureByTitle(delTitle: String, db: SQLiteDatabase){
        db!!.delete(DBHandler.FIRST_MEASURE_TABLE,"title=?", arrayOf(delTitle))
    }

    //Measure 테이블에서 하나의 정보를 삭제하는 함수
    fun delMeasure(db: SQLiteDatabase, title: String, trackIndex: Int, deleteMeasureIndex: Int) {
        val selectQuery=("SELECT MAX(MeasureIndex) FROM ${DBHandler.FIRST_MEASURE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex}")    //가장큰 마디 인덱스를 불러오는 sql문
        val cursor=db.rawQuery(selectQuery,null)
        var lastIndex: Int  //마지막 마디인덱스를 저장하기 위한 변수
        var startIndex: Int=deleteMeasureIndex  //삭제를 원하는 마디의 인덱스를 저장하는 변수
        var changeIndex=startIndex+1    //삭제된 위치 이후에 있는 정보들의 인덱스를 변경하기 위한 변수

        //sql문으로 불러온 가장 큰 마디의 인덱스를 변수에 저장
        if (cursor.moveToFirst()){
            lastIndex=cursor.getInt(0)
        }
        else{
            lastIndex=0
        }
        //삭제를 원하는 마디의 정보를 삭제
        db!!.delete(DBHandler.FIRST_MEASURE_TABLE,"Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=?", arrayOf(Integer.toString(deleteMeasureIndex)))

        //삭제된 인덱스가 비어있지 않도록 마디 인덱스들을 수정
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