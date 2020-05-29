package kr.sweetcase.harmoassist.modules.DBModule

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper
import kr.sweetcase.harmoassist.modules.DBModule.DBHandlers.*

//DB 조작을 위해 사용하는 클래스
class DBHandler(context: Context) :SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION){

    companion object{
        //DB정보과 테이블 이름들 지정
        private val DB_NAME="data"
        private val DB_VERSION=1

        public val FILEINFO_TABLE="FileInfo"
        public val SHEET_TABLE="Sheet"

        public val FIRST_MEASURE_TABLE="FirstMeasure"
        public val FIRST_HAMONICLINE_TABLE="FirstHamonicLine"
        public val FIRST_NOTE_TABLE="FirstNote"
        public val FIRST_ONECHORD_TABLE="FirstOneChord"
        public val LOG_TABLE="Log"

        public val TITLE="Title"
        public val SUMMARY="Summary"

        public val HAMONIC="Hamonic"
        public val TEMPO="Tempo"
        public val TEMPO_STYLE="TempoStyle"
        public val TIME_SIGNATURE="TimeSignature"

        public val MEASURE_INDEX="MeasureIndex"
        public val TRACK_INDEX="TrackIndex"

        public val CHORD="Chord"
        public val CHORD_STYLE="ChordStyle"

        public val NOTE_INDEX="NoteIndex"
        public val NOTE_STYLE="NoteStyle"
        public val DURATION="Duration"

        public val PITCH="Pitch"
        //public val ONECHORD_INDEX="OneChordIndex"
        public val OCTAVE="Octave"

        public val LOG_INDEX="LogIndex"
        public val EVENT="Event"
        public val DETAIL="Detail"

        //각 DB테이블들을 조작하기 위한 handler들
        val titleHandler=TitleDBHandler()
        val sheetHandler=SheetDBHandler()
        val measureHandler=MeasureDBHandler()
        val harmonicLineHandler=HarmonicLineDBHandler()
        val noteHandler=NoteDBHandler()
        val chordHandler=OneChordDBHandler()

    }

    //DB의 테이블들을 생성
    override fun onCreate(db: SQLiteDatabase?) {
        //db!!.execSQL("DROP TABLE IF EXISTS $FILEINFO_TABLE")
        val createFileinfoTable =
            ("CREATE TABLE $FILEINFO_TABLE ($TITLE TEXT PRIMARY KEY,$SUMMARY TEXT)")
        val createSheetTable =
            ("CREATE TABLE $SHEET_TABLE ($TITLE TEXT PRIMARY KEY, $HAMONIC TEXT, $TEMPO INT, $TIME_SIGNATURE TEXT, $TEMPO_STYLE TEXT)")

        val createFirstMeasureTable =//("CREATE TABLE $FIRST_MEASURE_TABLE ($TRACK_INDEX INT, $MEASURE_INDEX INT PRIMARY KEY, $TITLE TEXT)")
            ("CREATE TABLE $FIRST_MEASURE_TABLE ($TRACK_INDEX INT, $MEASURE_INDEX INT, $TITLE TEXT, PRIMARY KEY($TRACK_INDEX, $MEASURE_INDEX, $TITLE))")
        val createFirstHamonicLineTable =
            ("CREATE TABLE $FIRST_HAMONICLINE_TABLE ($TRACK_INDEX INT, $MEASURE_INDEX INT, $TITLE TEXT, $CHORD TEXT, $CHORD_STYLE INT, PRIMARY KEY($TRACK_INDEX, $MEASURE_INDEX, $TITLE))")
        val createFirstNoteTable =
            ("CREATE TABLE $FIRST_NOTE_TABLE ($TRACK_INDEX INT, $MEASURE_INDEX INT, $NOTE_INDEX INT, $TITLE TEXT, $NOTE_STYLE INT, $DURATION INT, PRIMARY KEY($TRACK_INDEX, $MEASURE_INDEX, $TITLE, $NOTE_INDEX))")
        val creteFirstOneChordTable =
            ("CREATE TABLE $FIRST_ONECHORD_TABLE ($TRACK_INDEX INT, $MEASURE_INDEX INT, $NOTE_INDEX INT, $PITCH TEXT, $TITLE TEXT, $OCTAVE TEXT, PRIMARY KEY($TRACK_INDEX, $MEASURE_INDEX, $TITLE, $NOTE_INDEX))")

        val createLogTable =
            ("CREATE TABLE $LOG_TABLE ($LOG_INDEX INT PRIMARY KEY, $EVENT INT, $DETAIL TEXT)")

        db!!.execSQL(createFileinfoTable)
        db!!.execSQL(createSheetTable)
        db!!.execSQL(createFirstMeasureTable)
        db!!.execSQL(createFirstHamonicLineTable)
        db!!.execSQL(createFirstNoteTable)
        db!!.execSQL(creteFirstOneChordTable)

        db!!.execSQL(createLogTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        /*db!!.execSQL("DROP TABLE IF EXISTS $FILEINFO_TABLE")
        onCreate(db!!)*/
    }

    //파일 정보를 추가하는 함수
    fun addInfo(title: String, summary: String){
        val db=this.writableDatabase
        val values = ContentValues()

        values.put(TITLE, title)
        values.put(SUMMARY, summary)

        db.insert(FILEINFO_TABLE, null, values)
        db.close()

    }

    //악보 정보를 추가하는 함수를 실행
    fun addSheet(title: String, harmonic: String, tempo: Int, timeSignature: String, tempoStyle: String){
        val db=this.writableDatabase

        sheetHandler.addSheet(db, title, harmonic, tempo, timeSignature, tempoStyle)

        db.close()
    }

    //음표 정보를 추가하는 함수들을 실행
    fun addNote(title: String, trackIndex: Int, measureIndex: Int, noteIndex: Int, octave: Int, pitch: String, duration: Int, noteStyle: Int){
        val db=writableDatabase

        noteHandler.addNote(db, title, trackIndex, measureIndex, noteIndex, duration, noteStyle)
        chordHandler.addChord(db, title, trackIndex, measureIndex, pitch, noteIndex, octave)

        db.close()
    }

    //화음 정보를 추가하는 함수들을 실행
    fun addHarmonicLine(title: String, trackIndex: Int, measureIndex: Int, chord: String, chordStyle: Int){
        val db=writableDatabase

        harmonicLineHandler.addHarmonicLine(db, title, trackIndex, measureIndex, chord, chordStyle)

        db.close()
    }

    //마디를 추가하는 함수를 실행
    fun addMeasure(title: String/*, chord: String, chordStyle: Int*/){
        val db=writableDatabase

        measureHandler.addMeasure(db, title ,1)
        measureHandler.addMeasure(db, title ,2)

        /*harmonicLineHandler.addHarmonicLine2(db, title, 1, chord, chordStyle)
        harmonicLineHandler.addHarmonicLine2(db, title, 2, chord, chordStyle)*/

        db.close()
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //파일을 삭제했을 때 DB에서 삭제하는 함수들을 실행
    fun deleteFileInfo(title: String){
        val db=writableDatabase

        chordHandler.deleteAllOneChordByTitle(title, db)
        noteHandler.deleteAllNoteByTitle(title, db)
        harmonicLineHandler.deleteAllHarmonicLineByTitle(title, db)
        measureHandler.deleteAllMeasureByTitle(title, db)
        sheetHandler.deleteAllSheetByTitle(title, db)
        titleHandler.deleteFile(title, db)

        db.close()
    }

    //음표를 삭제했을 때 DB에서 삭제하는 함수들을 실행
    fun deleteNote(title: String, trackIndex: Int, measureIndex: Int, deleteNoteIndex: Int){
        val db=writableDatabase

        chordHandler.delOneChord(db, title, trackIndex, measureIndex, deleteNoteIndex)
        noteHandler.delNote(db, title, trackIndex, measureIndex, deleteNoteIndex)

        db.close()
    }

    //마디를 삭제했을 때 DB에서 삭제하는 함수들을 실행
    fun deleteMeasure(title: String, trackIndex: Int, measureIndex: Int){
        val db=writableDatabase

        chordHandler.deleteOneChordByMeasure(db, title, measureIndex)
        noteHandler.deleteNoteByMeasure(db, title, measureIndex)
        harmonicLineHandler.delHarmonicLine(db, title, trackIndex, measureIndex)
        measureHandler.delMeasure(db, title, trackIndex, measureIndex)

        db.close()
    }

    fun testdata(){
        val db=this.writableDatabase
        val e=TitleDBHandler()
        val f=SheetDBHandler()
        val a=MeasureDBHandler()
        val b=HarmonicLineDBHandler()
        val c=NoteDBHandler()
        val d=OneChordDBHandler()
        val logm=LogModule()

        f.testdata(db)
        e.testdata(db)
        a.testdata(db)
        b.testdata(db)
        c.testdata(db)
        d.testdata(db)

        //logm.testdata(db)
        db.close()
    }

    fun testLog(){
        val db=this.writableDatabase
        val logModule=LogModule()

        //logModule.logCheck(db)

        db.close()
    }

    fun check(): List<String>{
        val db=this.writableDatabase
        val logModule=LogModule()

        val list=logModule.check(db)

        return list
    }

    //파일 목록 불러오기 함수
    fun getAllFileInfo(): Song {
        val db=readableDatabase
        val selectALLQuery=("SELECT * FROM $FILEINFO_TABLE ORDER BY $TITLE ASC")
        val selectALLQuery2=("SELECT * FROM $SHEET_TABLE ORDER BY $TITLE ASC")
        //val selectALLQuery3=("SELECT * FROM $FIRST_HAMONICLINE_TABLE ORDER BY $TITLE ASC")
        val cursor=db.rawQuery(selectALLQuery,null)
        val cursor2=db.rawQuery(selectALLQuery2,null)
        //val cursor3=db.rawQuery(selectALLQuery3,null)
        var songInfo: Song=Song()


        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
                    var fileInfo:TitleDBHandler = TitleDBHandler()
                    fileInfo.title=cursor.getString(cursor.getColumnIndex(TITLE))
                    fileInfo.summary=cursor.getString(cursor.getColumnIndex(SUMMARY))

                    songInfo.titleList.add(fileInfo)
                }while(cursor.moveToNext())
            }

            if(cursor2.moveToFirst()){
                do{
                    var sheetInfo=SheetDBHandler()
                    sheetInfo.harmonic=cursor2.getString(cursor2.getColumnIndex(HAMONIC))
                    sheetInfo.tempo=cursor2.getInt(cursor2.getColumnIndex(TEMPO))
                    sheetInfo.timeSignature=cursor2.getString(cursor2.getColumnIndex(TIME_SIGNATURE))

                    songInfo.sheetList.add(sheetInfo)
                }while(cursor2.moveToNext())
            }
        }
        cursor.close()
        db.close()

        return songInfo
    }

    //파일 정보를 변경했을때 DB 정보를 변경하는 함수를 실행
    fun updateFile(title: String, summary: String){
        val db=writableDatabase

        titleHandler.changeFile(db, title, summary)

        db.close()
    }

    //음표 정보를 변경했을때 DB 정보를 변경하는 함수를 실행
    fun updateNote(title: String, trackIndex: Int, measureIndex: Int, noteIndex: Int, duration: Int, noteStyle: Int){
        val db=writableDatabase

        noteHandler.changeNote(db, title, trackIndex, measureIndex, noteIndex, duration, noteStyle)

        db.close()
    }

    //화음 정보를 변경했을때 DB 정보를 변경하는 함수를 실행
    fun updateHarmonicLine(title: String, trackIndex: Int, measureIndex: Int, chord: String, chordStyle: Int){
        val db=writableDatabase

        harmonicLineHandler.updateHarmonicLine(db, title, trackIndex, measureIndex, chord, chordStyle)

        db.close()
    }

    //음표를 병합했을 때 DB 정보를 변경하는 함수를 실행
    fun mergeNote(title: String, trackIndex: Int, measureIndex: Int, mergeNoteIndex: Int, mergeNum: Int){
        val db=writableDatabase

        noteHandler.mergeNote(db, title, trackIndex, measureIndex, mergeNoteIndex, mergeNum)
        chordHandler.mergeChord(db, title, trackIndex, measureIndex, mergeNoteIndex, mergeNum)

        db.close()
    }

    //음표를 분할했을 때 DB 정보를 변경하는 함수를 실행
    fun seperateNote(title: String, trackIndex: Int, measureIndex: Int, seperateNoteIndex: Int, seperate: Int){
        val db=writableDatabase

        noteHandler.seperateNote(db, title, trackIndex, measureIndex, seperateNoteIndex, seperate)
        chordHandler.seperateChord(db, title, trackIndex, measureIndex, seperateNoteIndex, seperate)

        db.close()
    }

    //검색한 내용이 제목에 들어간 곡 목록을 불러오는 함수
    fun searchFileInfo(keyWord: String): Song{
        val db=readableDatabase
        val selectALLQuery=("SELECT * FROM $FILEINFO_TABLE WHERE $TITLE LIKE \"%$keyWord%\"")
        val selectALLQuery2=("SELECT * FROM $SHEET_TABLE ORDER BY $TITLE ASC")
        val cursor=db.rawQuery(selectALLQuery,null)
        val cursor2=db.rawQuery(selectALLQuery2,null)
        var songInfo: Song=Song()

        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
                    var fileInfo:TitleDBHandler = TitleDBHandler()
                    fileInfo.title=cursor.getString(cursor.getColumnIndex(TITLE))
                    fileInfo.summary=cursor.getString(cursor.getColumnIndex(SUMMARY))

                    songInfo.titleList.add(fileInfo)
                }while(cursor.moveToNext())
            }

            if(cursor2.moveToFirst()){
                do{
                    var sheetInfo=SheetDBHandler()
                    sheetInfo.harmonic=cursor2.getString(cursor2.getColumnIndex(HAMONIC))
                    sheetInfo.tempo=cursor2.getInt(cursor2.getColumnIndex(TEMPO))
                    sheetInfo.timeSignature=cursor2.getString(cursor2.getColumnIndex(TIME_SIGNATURE))

                    songInfo.sheetList.add(sheetInfo)
                }while(cursor2.moveToNext())
            }
        }
        cursor.close()
        db.close()

        return songInfo
    }

    //선택한 곡의 정보를 모두 불러오는 함수
    fun getSongInfo(titleName:String): Song{
        val db=readableDatabase

        var songInfo:Song=Song()
        var fileInfo:TitleDBHandler= TitleDBHandler()
        var sheetInfo:SheetDBHandler=SheetDBHandler()
        var measureInfo:MeasureDBHandler=MeasureDBHandler()
        var harmonicLineInfo1:HarmonicLineDBHandler= HarmonicLineDBHandler()
        var harmonicLineInfo2:HarmonicLineDBHandler= HarmonicLineDBHandler()
        var noteInfo1:NoteDBHandler= NoteDBHandler()
        var noteInfo2:NoteDBHandler= NoteDBHandler()
        var chordInfo1:OneChordDBHandler= OneChordDBHandler()
        var chordInfo2:OneChordDBHandler= OneChordDBHandler()

        val titleQuery=("SELECT * FROM $FILEINFO_TABLE WHERE $TITLE=\"${titleName}\"")
        val sheetQuery=("SELECT * FROM $SHEET_TABLE WHERE $TITLE=\"${titleName}\"")

        val measureQuery1=("SELECT MAX(MeasureIndex) FROM $FIRST_MEASURE_TABLE WHERE $TITLE=\"${titleName}\" AND $TRACK_INDEX=1 ORDER BY $MEASURE_INDEX ASC")
        //val measureQuery2=("SELECT * FROM $FIRST_MEASURE_TABLE WHERE $TITLE=\"${titleName}\" AND $TRACK_INDEX=2 ORDER BY $MEASURE_INDEX ASC")

        val harmonicQuery1=("SELECT * FROM $FIRST_HAMONICLINE_TABLE WHERE $TITLE=\"${titleName}\" AND $TRACK_INDEX=1 ORDER BY $MEASURE_INDEX ASC")
        val harmonicQuery2=("SELECT * FROM $FIRST_HAMONICLINE_TABLE WHERE $TITLE=\"${titleName}\" AND $TRACK_INDEX=2 ORDER BY $MEASURE_INDEX ASC")

        val noteQuery1=("SELECT * FROM $FIRST_NOTE_TABLE WHERE $TITLE=\"${titleName}\" AND $TRACK_INDEX=1 ORDER BY $MEASURE_INDEX ASC, $NOTE_INDEX")
        val noteQuery2=("SELECT * FROM $FIRST_NOTE_TABLE WHERE $TITLE=\"${titleName}\" AND $TRACK_INDEX=2 ORDER BY $MEASURE_INDEX ASC, $NOTE_INDEX")

        val chordQuery1=("SELECT * FROM $FIRST_ONECHORD_TABLE WHERE $TITLE=\"${titleName}\" AND $TRACK_INDEX=1 ORDER BY $MEASURE_INDEX ASC, $NOTE_INDEX")
        val chordQuery2=("SELECT * FROM $FIRST_ONECHORD_TABLE WHERE $TITLE=\"${titleName}\" AND $TRACK_INDEX=2 ORDER BY $MEASURE_INDEX ASC, $NOTE_INDEX")

        val titleCursor=db.rawQuery(titleQuery,null)
        val sheetCursor=db.rawQuery(sheetQuery,null)

        val measureCursor1=db.rawQuery(measureQuery1,null)
        //val measureCursor2=db.rawQuery(measureQuery2,null)

        val harmicCursor1=db.rawQuery(harmonicQuery1,null)
        val harmicCursor2=db.rawQuery(harmonicQuery2,null)

        val noteCursor1=db.rawQuery(noteQuery1,null)
        val noteCursor2=db.rawQuery(noteQuery2,null)

        val chordCursor1=db.rawQuery(chordQuery1,null)
        val chordCursor2=db.rawQuery(chordQuery2,null)

        if(titleCursor!=null){
            if(titleCursor.moveToFirst()){
                do{
                    fileInfo.title=titleCursor.getString(titleCursor.getColumnIndex(TITLE))
                    fileInfo.summary=titleCursor.getString(titleCursor.getColumnIndex(SUMMARY))
                }while(titleCursor.moveToNext())

                //악보정보 읽기
                if(sheetCursor.moveToFirst()){
                    sheetInfo.harmonic=sheetCursor.getString(titleCursor.getColumnIndex(HAMONIC))
                    sheetInfo.tempo=sheetCursor.getInt(titleCursor.getColumnIndex(TEMPO))
                    sheetInfo.timeSignature=sheetCursor.getString(titleCursor.getColumnIndex(TIME_SIGNATURE))
                    sheetInfo.tempoStyle=sheetCursor.getString(titleCursor.getColumnIndex(TEMPO_STYLE))

                    songInfo.sheetInfo=sheetInfo
                }

                //마디정보 읽기
                if(measureCursor1.moveToFirst()){
                    songInfo.measureNum=measureCursor1.getInt(0)   //마디의 갯수로 변경해야 함
                }

                //트랙1 화음정보 읽기
                if(harmicCursor1.moveToFirst()){
                    do{
                        harmonicLineInfo1.trackIndex=harmicCursor1.getInt(titleCursor.getColumnIndex(TRACK_INDEX))
                        harmonicLineInfo1.chord=harmicCursor1.getString(titleCursor.getColumnIndex(CHORD))
                        harmonicLineInfo1.measureIndex=harmicCursor1.getInt(titleCursor.getColumnIndex(MEASURE_INDEX))
                        harmonicLineInfo1.chordStyle=harmicCursor1.getInt(titleCursor.getColumnIndex(CHORD_STYLE))

                        songInfo.trackOneharmonicLineList.add(harmonicLineInfo1)
                    }while(harmicCursor1.moveToNext())
                }

                //트랙2 화음정보 읽기
                if(harmicCursor2.moveToFirst()){
                    do{
                        harmonicLineInfo2.trackIndex=harmicCursor2.getInt(titleCursor.getColumnIndex(TRACK_INDEX))
                        harmonicLineInfo2.chord=harmicCursor2.getString(titleCursor.getColumnIndex(CHORD))
                        harmonicLineInfo2.measureIndex=harmicCursor2.getInt(titleCursor.getColumnIndex(MEASURE_INDEX))
                        harmonicLineInfo2.chordStyle=harmicCursor2.getInt(titleCursor.getColumnIndex(CHORD_STYLE))

                        songInfo.trackTwoharmonicLineList.add(harmonicLineInfo2)
                    }while(harmicCursor2.moveToNext())
                }

                //트랙1 노트정보 읽기
                if(noteCursor1.moveToFirst()){
                    do{
                        noteInfo1.tarckIndex=noteCursor1.getInt(titleCursor.getColumnIndex(TRACK_INDEX))
                        noteInfo1.measrueIndex=noteCursor1.getInt(titleCursor.getColumnIndex(MEASURE_INDEX))
                        noteInfo1.duration=noteCursor1.getInt(titleCursor.getColumnIndex(DURATION))
                        noteInfo1.noteStyle=noteCursor1.getInt(titleCursor.getColumnIndex(NOTE_STYLE))

                        songInfo.trackOneNoteList.add(noteInfo1)
                    }while(noteCursor1.moveToNext())
                }

                //트랙2 노트정보 읽기
                if(noteCursor2.moveToFirst()){
                    do{
                        noteInfo2.tarckIndex=noteCursor2.getInt(titleCursor.getColumnIndex(TRACK_INDEX))
                        noteInfo2.measrueIndex=noteCursor2.getInt(titleCursor.getColumnIndex(MEASURE_INDEX))
                        noteInfo2.duration=noteCursor2.getInt(titleCursor.getColumnIndex(DURATION))
                        noteInfo2.noteStyle=noteCursor2.getInt(titleCursor.getColumnIndex(NOTE_STYLE))

                        songInfo.trackTwoNoteList.add(noteInfo2)
                    }while(noteCursor2.moveToNext())
                }

                //트랙1 음정보 읽기
                if(chordCursor1.moveToFirst()){
                    do{
                        chordInfo1.trackIndex=chordCursor1.getInt(titleCursor.getColumnIndex(TRACK_INDEX))
                        chordInfo1.measureIndex=chordCursor1.getInt(titleCursor.getColumnIndex(MEASURE_INDEX))
                        chordInfo1.noteIndex=chordCursor1.getInt(titleCursor.getColumnIndex(NOTE_INDEX))
                        chordInfo1.pitch=chordCursor1.getString(titleCursor.getColumnIndex(PITCH))
                        chordInfo1.otave=chordCursor1.getInt(titleCursor.getColumnIndex(OCTAVE))

                        songInfo.trackOnechordList.add(chordInfo1)
                    }while(chordCursor1.moveToNext())
                }

                //트랙2 음정보 읽기
                if(chordCursor2.moveToFirst()){
                    do{
                        chordInfo2.trackIndex=chordCursor2.getInt(titleCursor.getColumnIndex(TRACK_INDEX))
                        chordInfo2.measureIndex=chordCursor2.getInt(titleCursor.getColumnIndex(MEASURE_INDEX))
                        chordInfo2.noteIndex=chordCursor2.getInt(titleCursor.getColumnIndex(NOTE_INDEX))
                        chordInfo2.pitch=chordCursor2.getString(titleCursor.getColumnIndex(PITCH))
                        chordInfo2.otave=chordCursor2.getInt(titleCursor.getColumnIndex(OCTAVE))

                        songInfo.trackTwochordList.add(chordInfo2)
                    }while(chordCursor2.moveToNext())
                }
            }
        }

        titleCursor.close()
        sheetCursor.close()
        measureCursor1.close()
        //measureCursor2.close()
        harmicCursor1.close()
        harmicCursor2.close()
        noteCursor1.close()
        noteCursor2.close()
        chordCursor1.close()
        chordCursor2.close()

        db.close()

        return songInfo
    }
}
