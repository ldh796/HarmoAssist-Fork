package kr.sweetcase.harmoassist

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.midisheetmusic.ChooseSongActivity
import com.midisheetmusic.FileUri
import com.midisheetmusic.MidiFile
import com.midisheetmusic.SheetMusicActivity
import kotlinx.android.synthetic.main.activity_sheet_redirection.*
import kotlinx.coroutines.*
import kr.sweetcase.harmoassist.listMaterials.Music
import kr.sweetcase.harmoassist.modules.AIConnectionModule.AIClientTask
import kr.sweetcase.harmoassist.modules.AIConnectionModule.labels.RequestData
import org.json.JSONArray
import kotlin.coroutines.CoroutineContext

// TODO 여기에서 악보 인터페이스로 들어가는 코드 작성하면 됨.

/**
 * TODO 악보를 생성하는 경우는 다음과 같다.
 * 1. 새 악보 생성: 말 그대로 새 악보 생성
 * 2. AI를 이용한 새 악보 생성 : 새 악보를 생성하되 비어있는 악보가 아닌 작곡된 악보
 * 3. 기존에 있는 악보 생성: 작곡된 악보를 생성
 *
 * 인텐트 파라미터는 MakeSheetType 참고
 */



class SheetRedirectionActivity : AppCompatActivity(), CoroutineScope {


    private var backKeyClickTime : Long = 0
    private lateinit var musicInfoData : Music
    private var type : String? = null
    private var context : Context = this
    private var activity : Activity = this

    private var clientConnection: AIClientTask? = null

    // task
    private lateinit var mJob : Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main


    // Redirection Exception
    class RedirectionExceptionHandler(
        val dialogBuilder : AlertDialog.Builder,
        val activity : Activity,
        override val key: CoroutineContext.Key<*>
    ) : CoroutineExceptionHandler {

        override fun handleException(context: CoroutineContext, exception: Throwable) {
            // TODO 나중에 타입에 따라 다르게 설정해야 할 필요가 있음
            val alert = dialogBuilder
                .setTitle("ERROR")
                .setMessage(exception.message.toString())
                .setPositiveButton("확인", DialogInterface.OnClickListener {
                        dialog, _ ->
                    dialog.dismiss()
                    activity.finish()
                })
                .create()
            alert.show()
        }

    }

    //TODO Test용 함수
    fun makeTestMidiUri(testFileName : String) : FileUri {
        val newOutputStream = openFileOutput("tmp.mid", Context.MODE_PRIVATE)
        val inputFromAsset = resources.assets.open(testFileName)

        // 내부 저장소에 임시로 저장
        newOutputStream.write(inputFromAsset.readBytes())
        newOutputStream.close()

        // 임시로 저장된 파일 갖고오기
        val realFileStr = getFileStreamPath("tmp.mid")

        return FileUri(Uri.parse(realFileStr.absolutePath), null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet_redirection)

        // 타입 확인
        type = intent.extras?.getString("type")

        if(type != null) {
            when(type) {

                // 불러오기
                MakeSheetType.CURRENT.key -> {

                    mJob = Job()
                    val dialogBuilder = AlertDialog.Builder(this@SheetRedirectionActivity)


                    // Exception
                    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->

                        // TODO 나중에 타입에 따라 다르게 설정해야 할 필요가 있음
                        val alert = dialogBuilder
                            .setTitle("ERROR")
                            .setMessage(throwable.message.toString())
                            .setPositiveButton("확인", DialogInterface.OnClickListener {
                                    dialog, _ ->
                                dialog.dismiss()
                                finish()
                            })
                            .create()
                        alert.show()
                    }

                    launch(exceptionHandler) {
                        val deffered = async(Dispatchers.Default) {

                            musicInfoData = intent.extras?.getSerializable(MakeSheetType.CURRENT.intentKeys[0]) as Music
                            Log.d("activity test", "success")

                            // TODO 현재 있는 악보를 불러오는 경우
                            // TODO DB에 접속해서 모든 미디데이터를 불러오기

                            // TODO 악보 인터페이스 실행

                            // Test
                            val tmpFileUri = makeTestMidiUri("Oceanicism.mid")
                            doOpenFile(tmpFileUri, musicInfoData)
                        }
                        deffered.await()
                    }
                }
                // 새 악보 생성
                MakeSheetType.NEW.key -> {

                    mJob = Job()
                    val dialogBuilder = AlertDialog.Builder(this@SheetRedirectionActivity)
                    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->

                        // TODO 나중에 타입에 따라 다르게 설정해야 할 필요가 있음
                        val alert = dialogBuilder
                            .setTitle("ERROR")
                            .setMessage(throwable.message.toString())
                            .setPositiveButton("확인", DialogInterface.OnClickListener {
                                    dialog, _ ->
                                dialog.dismiss()
                                finish()
                            })
                            .create()
                        alert.show()
                    }

                    // 코루틴 실행부
                    launch(exceptionHandler) {
                        val differed = async(Dispatchers.Default) {

                            // intent 데이터 추출
                            musicInfoData =
                                intent.extras?.getSerializable(MakeSheetType.NEW.intentKeys[0]) as Music
                            val emptyMeasure =
                                intent.extras?.getInt(MakeSheetType.EMPTY_MEASURE.intentKeys[0]) as Int

                            // Asset에서 파일 긁어옴 <Test>
                            // TODO 원래 이 위치에서 MidiFile로 Midi파일 생성해서 저장해야 됨
                            val tmpFileUri = makeTestMidiUri("Chopin__Waltz_Op._64_No._2_in_Csharp_minor.mid")
                            // 파일열고 액티비티 전환
                            doOpenFile(tmpFileUri, musicInfoData)

                        }
                        differed.await()
                    }


                }
                MakeSheetType.NEW_AI.key -> {

                    // intent 데이터 추출
                    mJob = Job()

                    // 핸들러 설정
                    val dialogBuilder = AlertDialog.Builder(this@SheetRedirectionActivity)
                    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->

                        // TODO 나중에 타입에 따라 다르게 설정해야 할 필요가 있음
                        val alert = dialogBuilder
                            .setTitle("ERROR")
                            .setMessage(throwable.message.toString())
                            .setPositiveButton("확인", DialogInterface.OnClickListener {
                                    dialog, _ ->
                                dialog.dismiss()
                                finish()
                            })
                            .create()
                        alert.show()
                    }

                    launch(exceptionHandler) {
                        val deferred = async(Dispatchers.Default) {

                            musicInfoData = intent.extras?.getSerializable(MakeSheetType.NEW_AI.intentKeys[0]) as Music
                            val aiOptionStr = intent.extras?.getString(MakeSheetType.NEW_AI.intentKeys[1]) as String
                            val noteSize = intent.extras?.getInt(MakeSheetType.NEW_AI.intentKeys[2]) as Int

                            // text 변경
                            loading_test.text = "서버 연결 중.."

                            // 서버 접속
                            clientConnection = AIClientTask.Builder()
                                .setContext(context)
                                .setHost("sweetcase.tk")
                                .setPort(7890)
                                .setPswd("4680")
                                .setSerial("avbk2#$@skd#%")
                                .build()
                            clientConnection?.connect()

                            // 서버 접속 확인
                            if(clientConnection!!.connected) {
                                loading_test.text = "요청 데이터 송신중"

                                // TODO 서버에 딥러닝 요청 데이터 전송 및 수신
                                val requestJson = RequestData(
                                    clientConnection?.clientInfo!!.myIP,
                                    aiOptionStr,
                                    musicInfoData.timeSignature,
                                    noteSize
                                )
                                clientConnection?.sendRequestData(requestJson)

                                // TODO 데이터 받기
                                val rawData = clientConnection?.receiveResultRawData()

                                // TODO 데이터 나열(알고리즘 구현)
                                loading_test.text = "데이터 나열 중..."

                                // TODO Midi파일이 제데로 저장이 되었는지 테스트할 필요가있다.
                                try {
                                    val os = openFileOutput("result.mid", Context.MODE_PRIVATE)
                                    os.write(rawData?.toByteArray())
                                } catch(e : Exception) {
                                    throw Exception("파일을 저장하는 데 실패했습니다.")
                                }

                                val iis = openFileInput("result.mid")
                                // TODO 미디파일이 제대로 입력되었는지 확인할 필요가 있음


                                // TODO 반주 생성

                                // TODO 서버 연결 끊기
                                loading_test.text = "서버와의 연결을 끊는 중"
                                clientConnection?.disconnect()

                                // TODO DB에 저장
                                loading_test.text = "데이터베이스에 저장중"

                                // TODO 악보 인터페이스 실행

                                val tmpFileUri = makeTestMidiUri("Chopin__Waltz_Op._64_No._2_in_Csharp_minor.mid")
                                doOpenFile(tmpFileUri, musicInfoData)
                            } else {
                                throw Exception("서버와의 연결에 실패했습니다.")
                            }
                        }
                        deferred.await()
                    }
                }
            }
        }
    }

    // TODO 뒤로 가기 버튼을 눌렀을 경우
    override fun onBackPressed() {
        if(System.currentTimeMillis() > backKeyClickTime + 2000) {
            backKeyClickTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            // TODO 상황 정리
            // 종료
            mJob.cancel()
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
        if(this.clientConnection != null) {
            if(this.clientConnection!!.connected) {
                this.clientConnection!!.conn.shutdown()
            }
        }
    }

    /************************* 여기서부터 악보생성 관련 함수들 *********************************/
    fun doOpenFile(file: FileUri, musicData : Music) {
        val data = file.getData(this)
        if (data == null || data.size <= 6 || !MidiFile.hasMidiHeader(data)) {
            ChooseSongActivity.showErrorDialog(
                "Error: Unable to open song: $file",
                this
            )
            return
        }
        updateRecentFile(file)
        val intent = Intent(
            Intent.ACTION_VIEW, file.uri, this,
            SheetMusicActivity::class.java
        )
        intent.putExtra(SheetMusicActivity.MidiTitleID, file.toString())
        intent.putExtra("music_info", musicData)
        startActivity(intent)

        // 액티비티 종료
        this.finish()
    }

    fun updateRecentFile(recentfile: FileUri) {
        try {
            val settings = getSharedPreferences("midisheetmusic.recentFiles", 0)
            val editor = settings.edit()
            var prevRecentFiles: JSONArray? = null
            val recentFilesString = settings.getString("recentFiles", null)
            prevRecentFiles = recentFilesString?.let { JSONArray(it) } ?: JSONArray()
            val recentFiles = JSONArray()
            val recentFileJson = recentfile.toJson()
            recentFiles.put(recentFileJson)
            for (i in 0 until prevRecentFiles.length()) {
                if (i >= 10) {
                    break // only store 10 most recent files
                }
                val file = prevRecentFiles.getJSONObject(i)
                if (!FileUri.equalJson(recentFileJson, file)) {
                    recentFiles.put(file)
                }
            }
            editor.putString("recentFiles", recentFiles.toString())
            editor.apply()
        } catch (e: java.lang.Exception) {
        }
    }

}
