package kr.sweetcase.harmoassist.modules.AIConnectionModule.labels

import kr.sweetcase.harmoassist.modules.AIConnectionModule.DataModel
import org.json.JSONObject
import java.lang.NullPointerException

class RequestData : DataModel {

    var myIP: String
    var genre: String
    var timeSignature: String
    var noteSize = 0

    constructor(myIP : String, genre : String?, timeSignature : String, noteSize : Int?) : super() {

        if(genre != null) {
            if(noteSize != null) {
                this.myIP = myIP
                this.genre = genre
                this.timeSignature = timeSignature
                this.noteSize = noteSize
            } else {
                throw NullPointerException("note size is null")
            }
        } else {
            throw NullPointerException("genre is null")
        }
    }
    override fun makeToJson() : String {
        return JSONObject().put("type", 1)
            .put("myIP", this.myIP)
            .put("genre", this.genre)
            .put("timeSignature", "_"+this.timeSignature)
            .put("noteSize", this.noteSize).toString()
    }
}