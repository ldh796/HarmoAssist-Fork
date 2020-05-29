package kr.sweetcase.harmoassist.modules.AIConnectionModule

import android.content.Context
import android.net.Uri
import android.net.wifi.aware.SubscribeConfig
import android.os.AsyncTask
import android.util.Log
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisConnectionException
import io.lettuce.core.RedisURI
import io.lettuce.core.pubsub.RedisPubSubAdapter
import kr.sweetcase.harmoassist.modules.AIConnectionModule.labels.ConnectionData
import kr.sweetcase.harmoassist.modules.AIConnectionModule.labels.RedisClientData
import kr.sweetcase.harmoassist.modules.AIConnectionModule.labels.RedisServerData
import kr.sweetcase.harmoassist.modules.AIConnectionModule.labels.RequestData
import java.net.ConnectException

// AI 수행 클라이언트 클래스
class AIClientTask {
    var conn : RedisClient //연결이 없는 상태일 때는 null유지
    var serverInfo : RedisServerData
    var clientInfo: RedisClientData
    var connected : Boolean = false
    var key : String? = null

    // Static Value
    companion object {
        const val SERVER_CHANNEL = "DeepServerPipe"
        const val IPMAP = "IPMap"
    }

    private constructor(connections : RedisClient, serverInfo : RedisServerData, clientInfo : RedisClientData) {
        this.serverInfo = serverInfo
        this.clientInfo = clientInfo
        this.conn = connections
    }

    // AIClient Builder
    class Builder {
        private lateinit var context: Context
        private var host : String = ""
        private var port : Int = 0
        private var pswd : String = ""
        private var serial : String = ""


        // TODO 유효성 판정
        fun setContext(context : Context) : Builder { this.context = context; return this }
        fun setHost(host : String) : Builder { this.host = host; return this  }
        fun setPort(port : Int) : Builder { this.port = port; return this  }
        fun setPswd(pswd : String) : Builder { this.pswd = pswd; return this  }
        fun setSerial(serial : String) : Builder { this.serial = serial; return this  }

        fun build() : AIClientTask {
            try {
                // TODO 데이터 유효성 판정
                val clientInfo = RedisClientData()
                val serverInfo = RedisServerData(this.host, this.port, this.pswd, this.serial)

                // Redis 생성
                val uri : RedisURI = RedisURI.Builder.redis(this.host)
                    .withPassword(this.pswd)
                    .withPort(this.port)
                    .withDatabase(0)
                    .build()
                val conn = RedisClient.create(uri)
                return AIClientTask(conn, serverInfo, clientInfo)
            } catch(e : Exception) {
                throw e
            }
        }
    }

    // 연결 시도
    fun connect() {
        // 이미 연결되어있는 지 확인
        if(this.connected) {
            throw ConnectException("aleady connected")
        }
        try {
            // 연결
            this.conn.connect()
            val connection = this.conn.connectPubSub().sync()

            // 연결 요청 Json 송신
            val requestConnectJson
                    = ConnectionData(this.clientInfo.myIP, this.serverInfo.serial).makeToJson()

            // Redis Server가 IPMap을 만들지 않았을 때 0L를 호출한다.
            if(connection.publish(SERVER_CHANNEL, requestConnectJson) == 0L) {
                throw RedisConnectionException("서버에 응답이 없습니다.")
            }

            var connectLimitCounter = 0
            val connectLimit = 10

            // 서버로부터 키를 받음
            while(connectLimitCounter < connectLimit) {
                var rawKey = connection.hget(IPMAP, this.clientInfo.myIP)
                if(rawKey != null) {
                    try {
                        this.key = rawKey

                    } catch (e : TypeCastException) {
                        // 서버로부터 잘못된 값이 생성
                        // TODO 서버 문제 관련 문제를 관리자에게 송신할 프로세스를 생성
                        throw e
                    }
                    this.connected = true
                    break
                }
                else {
                    // 다시 받을 때 까지 기다림
                    Thread.sleep(1000L)
                    connectLimitCounter++
                }
            }
            // 서버 연결에 실패
            if(!this.connected) {
                throw RedisConnectionException("서버가 응답이 없습니다.")
            } else {
                this.connected = true
            }

        } catch(e : Exception) {
            // TODO 연결 실패에 대한 후처리
            throw e
        }
    }

    // 연결 헤제
    fun disconnect() {
        if(!this.connected) {
            throw ConnectException("aleady disconnected")
        }
        try {
            // 연결 해제 요청
            val connection = this.conn.connectPubSub().sync()
            val requestDisconnectJson =
                ConnectionData(this.clientInfo.myIP, this.serverInfo.serial, false).makeToJson()

            // Redis Server가 IPMap을 만들지 않았을 때 0L를 호출한다.
            if(connection.publish(SERVER_CHANNEL, requestDisconnectJson) == 0L) {
                throw RedisConnectionException("서버에 응답이 없습니다.")
            }

            Log.d("reidscontrol", connection.unsubscribe(this.key).toString())

            // SubScribe 해제
        } catch(e : Exception) {
            throw e
        }
        this.conn.shutdown()
        this.connected = false
    }

    // 딥러닝 요청 데이터 전송
    // TODO 원래 리턴값은 데이터 모델
    fun sendRequestData(requestData : RequestData) {
        if(!this.connected) {
            throw ConnectException("aleady disconnected")
        }
        try {
            // 서버에 요청 데이터 전송
            val mainConnection = this.conn.connectPubSub()
            val connection = mainConnection.sync() // 동기화 설정
            if(connection.publish(SERVER_CHANNEL, requestData.makeToJson()) == 0L) {
                // 정상적으로 전송했으면 1, 실패시 0
                throw RedisConnectionException("서버에 응답이 없습니다.")
            }
        } catch (e : Exception) {
            // TODO 예외처리
            throw e
        }
    }

    // 서버로부터 문자열 데이터 받기(이후에 midiFile로 변환해야함)
    fun receiveResultRawData() : String {
        val mainConnection = this.conn.connectPubSub()
        val listener = SubScribeListener(100)
        mainConnection.addListener(listener)

        // 동기화 설정하고 유저 키로 구독 시작
        val connection = mainConnection.sync()
        connection.subscribe(this.key)

        var timeoutCounter = 0
        val TIMEOUT = 60

        // 1분동안 기다림
        while(timeoutCounter < TIMEOUT) {
            val data = listener.getData()?.message

            if(data != null) {
                Log.d("test-redis", data)
                // 받으면 리턴
                return data
            } else {
                Log.d("test-redis", "failed")
                Thread.sleep(1000L)
                timeoutCounter++
            }
        }
        throw RedisConnectionException("서버와의 연결이 끊어졌습니다.")
    }
}