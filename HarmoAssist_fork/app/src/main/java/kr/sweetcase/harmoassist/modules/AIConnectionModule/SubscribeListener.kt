package kr.sweetcase.harmoassist.modules.AIConnectionModule

import io.lettuce.core.pubsub.RedisPubSubAdapter
import java.util.*

/*
    서버로부터 데이터를 받기 위한
    리스너 클래스
 */
class SubScribeListener : RedisPubSubAdapter<String, String> {
    class ReceivedData(val channel: String, val message: String)

    private var dataQueue = LinkedList<ReceivedData>()
    private var maxSize = 1
    constructor(maxSize: Int) : super() {
        this.maxSize = maxSize
    }

    // 여길 통해서 Redis 로부터 데이터를 받는다.
    override fun message(channel: String?, message: String?) {

        super.message(channel, message)
        if(( channel != null).and(message != null)) {
            if( dataQueue.size < maxSize)
                dataQueue.add(ReceivedData(channel!!, message!!))
        }
    }
    fun getData() : ReceivedData? {
        return if(dataQueue.isEmpty())
            null
        else {
            val resultData = this.dataQueue[0]
            dataQueue.removeAt(0)
            resultData
        }
    }
}