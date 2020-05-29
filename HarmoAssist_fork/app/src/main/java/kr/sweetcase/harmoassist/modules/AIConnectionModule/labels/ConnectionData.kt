package kr.sweetcase.harmoassist.modules.AIConnectionModule.labels

import kr.sweetcase.harmoassist.modules.AIConnectionModule.DataModel
import org.json.JSONObject

class ConnectionData(val myIP : String, val serial : String, val isConnectSignal : Boolean = true) : DataModel() {

    override fun makeToJson() : String {
        val jsonObj = JSONObject()
        if(isConnectSignal)
            jsonObj.put("type", 0)
        else
            jsonObj.put("type", -1)
        jsonObj.put("myIP", myIP)
        jsonObj.put("serial", serial)
        return jsonObj.toString()
    }
}