package kr.sweetcase.harmoassist.modules.AIConnectionModule.labels

import android.app.Service
import android.content.Context
import android.net.wifi.WifiManager
import android.os.AsyncTask
import java.net.Socket

class RedisClientData {
    var myIP = ""
    constructor() {

        // 안드로이드 고정 IP 갖고오기
        myIP = ""

        // 와이파이 테스트 연결해서 연결 안되면 예외처리
        try {

            val socketThread = Thread(Runnable {
                kotlin.run {
                    val socket = Socket("www.google.com", 80)
                    myIP = socket.localAddress.toString()
                }
            })
            socketThread.start()
            socketThread.join()
        } catch(e : Exception) {
            throw e
        }
    }
}