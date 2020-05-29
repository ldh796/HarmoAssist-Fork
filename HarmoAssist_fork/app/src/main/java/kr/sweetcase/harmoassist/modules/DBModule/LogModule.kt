package kr.sweetcase.harmoassist.modules.DBModule

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kr.sweetcase.harmoassist.modules.DBModule.DBHandlers.*


//event
//1: 마디 추가
//2: 마디 삭제
//3: 화음 생성
//4: 화음 수정
//5: 노트 분할
//6: 노트 합병
//7: 음표 추가
//8: 음표 삭제

class LogModule {

    //신경 안써도 되는 함수
    fun writeLog(db: SQLiteDatabase, event: Int, string: String, title: String, trackIndex: Int){
        val selectQuery=("SELECT MAX(LogIndex) FROM ${DBHandler.LOG_TABLE} WHERE Title=\"${title}\" AND TrackIndex=${trackIndex}")
        val cursor=db.rawQuery(selectQuery,null)
        var index: Int

        if (cursor.moveToFirst()){
            index=cursor.getInt(0)
        }
        else{
            index=0
        }

        var detail: String
                ="${trackIndex}%"

        val values = ContentValues()

        values.put(DBHandler.EVENT, event)
        values.put(DBHandler.LOG_INDEX, index+1)
        values.put(DBHandler.DETAIL, detail)

        db!!.insert(DBHandler.FIRST_MEASURE_TABLE, null, values)
    }

    //마디 추가 삭제 로그를 DB에 저장하는 함수
    fun measureLog(db: SQLiteDatabase, event: Int, title: String, trackIndex: Int, measureIndex: Int) {
        val selectQuery=("SELECT MAX(LogIndex) FROM ${DBHandler.LOG_TABLE}")
        val cursor=db.rawQuery(selectQuery,null)
        var index: Int

        if (cursor.moveToFirst()){
            index=cursor.getInt(0)
        }
        else{
            index=0
        }

        val values = ContentValues()

        var detail="${title}%${trackIndex}%${measureIndex}%"

        values.put(DBHandler.EVENT, event)
        values.put(DBHandler.LOG_INDEX, index+1)
        values.put(DBHandler.DETAIL, detail)

        db!!.insert(DBHandler.LOG_TABLE, null, values)
    }

    //노트 분할 로그를 DB에 저장하는 함수
    fun noteSeperateLog(db: SQLiteDatabase, event: Int, title: String, trackIndex: Int, measureIndex: Int, noteIndex: Int, lastIndex: Int, num: Int/*분할할 갯수*/) {
        val selectQuery=("SELECT MAX(${DBHandler.LOG_INDEX}) FROM ${DBHandler.LOG_TABLE}")
        val cursor=db.rawQuery(selectQuery,null)
        var index: Int

        if (cursor.moveToFirst()){
            index=cursor.getInt(0)
        }
        else{
            index=0
        }

        val values = ContentValues()

        var detail="${title}%${trackIndex}%${measureIndex}%${noteIndex}%${lastIndex}%${num}%"

        values.put(DBHandler.EVENT, event)
        values.put(DBHandler.LOG_INDEX, index+1)
        values.put(DBHandler.DETAIL, detail)

        db!!.insert(DBHandler.LOG_TABLE, null, values)
    }

    //노트 합병 로그를 DB에 저장하는 함수
    fun noteMergeLog(db: SQLiteDatabase, event: Int, title: String, trackIndex: Int, measureIndex: Int, noteIndex: Int, lastIndex: Int, num: Int) {
        val selectQuery=("SELECT MAX(${DBHandler.LOG_INDEX}) FROM ${DBHandler.LOG_TABLE}")
        val cursor=db.rawQuery(selectQuery,null)
        var index: Int

        if (cursor.moveToFirst()){
            index=cursor.getInt(0)
        }
        else{
            index=0
        }

        val values = ContentValues()

        var detail="${title}%${trackIndex}%${measureIndex}%${noteIndex}%${lastIndex}%${num}%"

        values.put(DBHandler.EVENT, event)
        values.put(DBHandler.LOG_INDEX, index+1)
        values.put(DBHandler.DETAIL, detail)

        db!!.insert(DBHandler.LOG_TABLE, null, values)

    }

    //음표 추가 삭제 로그를 DB에 저장하는 함수
    fun chordLog(db: SQLiteDatabase, event: Int, title: String, trackIndex: Int, measureIndex: Int, noteIndex: Int, octave: Int, pitch: String, duration: Int, noteStyle: Int) {
        val selectQuery=("SELECT MAX(${DBHandler.LOG_INDEX}) FROM ${DBHandler.LOG_TABLE}")
        val cursor=db.rawQuery(selectQuery,null)
        var index: Int

        if (cursor.moveToFirst()){
            index=cursor.getInt(0)
        }
        else{
            index=0
        }

        val values = ContentValues()

        var detail="${title}%${trackIndex}%${measureIndex}%${noteIndex}%${octave}%${pitch}%${duration}%${noteStyle}%"

        values.put(DBHandler.EVENT, event)
        values.put(DBHandler.LOG_INDEX, index+1)
        values.put(DBHandler.DETAIL, detail)

        db!!.insert(DBHandler.LOG_TABLE, null, values)

    }

    //화음 생성 수정 로그를 DB에 저장하는 함수
    fun harmonicLog(db: SQLiteDatabase, event: Int, title: String, trackIndex: Int, measureIndex: Int, chord: String, chordStyle: Int) {
        val selectQuery=("SELECT MAX(${DBHandler.LOG_INDEX}) FROM ${DBHandler.LOG_TABLE}")
        val cursor=db.rawQuery(selectQuery,null)
        var index: Int

        if (cursor.moveToFirst()){
            index=cursor.getInt(0)
        }
        else{
            index=0
        }

        val values = ContentValues()

        var detail="${title}%${trackIndex}%${measureIndex}%${chord}%${chordStyle}%"

        values.put(DBHandler.EVENT, event)
        values.put(DBHandler.LOG_INDEX, index+1)
        values.put(DBHandler.DETAIL, detail)

        db!!.insert(DBHandler.LOG_TABLE, null, values)
    }

    //신경 안써도 되는 함수
    fun testdata(db: SQLiteDatabase){

        measureLog(db, 2, "title1", 1, 3)
        measureLog(db, 2, "title1", 1, 4)
        measureLog(db, 2, "title1", 2, 3)
        measureLog(db, 2, "title1", 2, 4)

        harmonicLog(db, 3, "title2", 2, 5, "chord12", 123 )

        harmonicLog(db, 4, "title2", 2, 2, "chord123", 113 )

        noteSeperateLog(db, 5, "title2", 2, 4, 1,4,2)

        noteMergeLog(db, 6, "title2", 2, 3, 1, 4, 3)

        //chordLog(db, 7, "title1", 1 ,1, 5, 3, "pitch12")

        //chordLog(db, 8, "title1", 1 ,1, 2, 3, "pitch12")

    }

    //DB에 저장되어 있는 로그를 불러들어오는 함수
    fun logCheck(db: SQLiteDatabase, handler: DBHandler){
        val selectQuery=("SELECT * FROM ${DBHandler.LOG_TABLE} ORDER BY ${DBHandler.LOG_INDEX} ASC")
        val cursor=db.rawQuery(selectQuery,null)
        var event: Int
        var str: String
        var detail: List<String>

        if (cursor.moveToFirst()){
            do{
                event=cursor.getInt(cursor.getColumnIndex(DBHandler.EVENT))
                str=cursor.getString(cursor.getColumnIndex(DBHandler.DETAIL))
                detail=str.split("%")
                logProcess(db, event, detail, handler)
            }while(cursor.moveToNext())
        }
        else{
        }
    }

    //신경 안써도 되는 함수
    fun check(db: SQLiteDatabase): List<String>{
        val selectQuery=("SELECT * FROM ${DBHandler.LOG_TABLE} ORDER BY ${DBHandler.LOG_INDEX} ASC")
        val cursor=db.rawQuery(selectQuery,null)
        var event: Int

        var detail: List<String>

        if(cursor.moveToFirst()) {
            val str: String = cursor.getString(cursor.getColumnIndex(DBHandler.DETAIL))
            detail=str.split("%")
            return detail
        }
        else{
            val str="null"
            detail=str.split("%")
        }

        return detail
    }

    //로그에 등록된 이벤트에 따라서 해당하는 DB에 대한 동작을 수행하는 함수
    fun logProcess(db: SQLiteDatabase, event: Int, detail: List<String>, handler: DBHandler){
        if(event==1){   //마디 추가
            val measure=MeasureDBHandler()
            //var detail="${title}%${trackIndex}%${measureIndex}%"
            handler.addMeasure(detail[0])
            //handler.addMeasure(detail[0], Integer.parseInt(detail[1]))
        }
        else if(event==2){  //마디 삭제
            val measure=MeasureDBHandler()
            handler.deleteMeasure(detail[0], Integer.parseInt(detail[1]), Integer.parseInt(detail[2]))
        }
        else if(event==3){  //화음 생성
            val harmonic=HarmonicLineDBHandler()
            handler.addHarmonicLine(detail[0], Integer.parseInt(detail[1]), Integer.parseInt(detail[2]), detail[3], Integer.parseInt(detail[4]))
        }
        else if(event==4){  //화음 수정
            val harmonic=HarmonicLineDBHandler()
            handler.updateHarmonicLine(detail[0], Integer.parseInt(detail[1]), Integer.parseInt(detail[2]), detail[3], Integer.parseInt(detail[4]))
        }
        else if(event==5){  //노트 분할
            val note=NoteDBHandler()
            handler.seperateNote(detail[0], Integer.parseInt(detail[1]), Integer.parseInt(detail[2]), Integer.parseInt(detail[3]), Integer.parseInt(detail[5]))
        }
        else if(event==6){  //노트 합병
            val note=NoteDBHandler()
            handler.mergeNote(detail[0], Integer.parseInt(detail[1]), Integer.parseInt(detail[2]), Integer.parseInt(detail[3]), Integer.parseInt(detail[5]))
        }
        else if(event==7){  //음표 추가
            val chord=OneChordDBHandler()
            handler.addNote(detail[0], Integer.parseInt(detail[1]), Integer.parseInt(detail[2]), Integer.parseInt(detail[3]), Integer.parseInt(detail[4]), detail[5], Integer.parseInt(detail[6]), Integer.parseInt(detail[7]))
        }
        else if(event==8){  //음표 삭제
            val chord=OneChordDBHandler()
            handler.deleteNote(detail[0], Integer.parseInt(detail[1]), Integer.parseInt(detail[2]), Integer.parseInt(detail[3]))
        }
        else{}
    }

}