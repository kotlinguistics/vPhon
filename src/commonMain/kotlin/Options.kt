data class Options(
    val delimit:String,
    val dialect:String,
    val glottal:Boolean=false,
    val pham:Boolean=false,
    val cao:Boolean=false,
    val palatals:Boolean=false,
    val tokenize:Boolean=false,
    val output_ortho:Boolean=false
)