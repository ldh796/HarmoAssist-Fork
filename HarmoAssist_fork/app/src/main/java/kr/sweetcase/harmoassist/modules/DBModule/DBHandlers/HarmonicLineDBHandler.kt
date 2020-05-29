package kr.sweetcase.harmoassist.modules.DBModule.DBHandlers

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kr.sweetcase.harmoassist.modules.DBModule.DBHandler

//DB HarmonicLine 테이블을 사용하기 위한 클래스
class HarmonicLineDBHandler {
    /*var chord:String=""
    var chordStyle:String=""
    var title:String=""
    var trackIndex:Int=0
    var MeasureIndex:Int=0*/


    //fun addHarmonicLine(db: SQLiteDatabase, harmonicLine: HarmonicLine, title: String){
        var trackIndex: Int=0
        var measureIndex: Int=0
        var chord: String=""
        var chordStyle: Int =0

    //DB에 HarmonicLine 정보를 추가하는 함수
    fun addHarmonicLine(db: SQLiteDatabase, title: String, trackIndex: Int, measureIndex: Int, chord: String, chordStyle: Int){

        val values = ContentValues()

        values.put(DBHandler.TRACK_INDEX, trackIndex)
        values.put(DBHandler.MEASURE_INDEX, measureIndex)
        values.put(DBHandler.TITLE, title)
        values.put(DBHandler.CHORD, chord)
        values.put(DBHandler.CHORD_STYLE, chordStyle)

        db!!.insert(DBHandler.FIRST_HAMONICLINE_TABLE, null, values)
    }

    //예비용
    fun addHarmonicLine2(db: SQLiteDatabase, title: String, trackIndex: Int, chord: String, chordStyle: Int){
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
        values.put(DBHandler.MEASURE_INDEX, index+1)
        values.put(DBHandler.TITLE, title)
        values.put(DBHandler.CHORD, chord)
        values.put(DBHandler.CHORD_STYLE, chordStyle)

        db!!.insert(DBHandler.FIRST_HAMONICLINE_TABLE, null, values)
    }

    //HarmonicLine 테이블에서 해당하는 파일 이름을 가진 정보를 삭제하는 함수
    fun deleteAllHarmonicLineByTitle(delTitle: String, db: SQLiteDatabase){
        db!!.delete(DBHandler.FIRST_HAMONICLINE_TABLE,"title=?", arrayOf(delTitle))
    }

    //HarmonicLine 테이블에서 하나의 정보를 삭제하는 함수
    fun delHarmonicLine(db: SQLiteDatabase, title: String, trackIndex: Int, deleteMeasureIndex: Int) {
        val selectQuery=("SELECT MAX(MeasureIndex) FROM ${DBHandler.FIRST_HAMONICLINE_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex}")    //가장큰 화음 인덱스를 불러오는 sql문
        val cursor=db.rawQuery(selectQuery,null)
        var lastIndex: Int  //마지막 마디인덱스를 저장하기 위한 변수
        var startIndex: Int=deleteMeasureIndex  //삭제를 원하는 화음의 마디 인덱스를 저장하는 변수
        var changeIndex=startIndex+1    //삭제된 위치 이후에 있는 정보들의 인덱스를 변경하기 위한 변수

        //sql문으로 불러온 가장 큰 마디의 인덱스를 변수에 저장
        if (cursor.moveToFirst()){
            lastIndex=cursor.getInt(0)
        }
        else{
            lastIndex=0
        }
        //삭제를 원하는 마디의 정보를 삭제
        db!!.delete(DBHandler.FIRST_HAMONICLINE_TABLE,"Title=\"${title}\" AND TrackIndex=${trackIndex} AND MeasureIndex=?", arrayOf(Integer.toString(deleteMeasureIndex)))

        //삭제된 인덱스가 비어있지 않도록 인덱스들을 수정
        while(startIndex<lastIndex) {
            var updateSql =
                ("UPDATE ${DBHandler.FIRST_HAMONICLINE_TABLE} SET MeasureIndex=$startIndex WHERE Title=\"${title}\" AND TrackIndex=$trackIndex AND MeasureIndex=$changeIndex")
            db.execSQL(updateSql)
            startIndex++
            changeIndex++
        }
    }

    //HarmonicLine 테이블에서 마디 인덱스를 수정하는 함수
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
    }
}