package kr.sweetcase.harmoassist

enum class MakeSheetType(val key : String, val intentKeys : Array<String>) {
    NEW ("new sheet", arrayOf("new music data")),
    CURRENT ("current sheet", arrayOf("current music data")),
    NEW_AI ("new ai sheet", arrayOf("new music data", "ai option", "note size")),
    EMPTY_MEASURE("emtpy measure", arrayOf("empty measure"))
}