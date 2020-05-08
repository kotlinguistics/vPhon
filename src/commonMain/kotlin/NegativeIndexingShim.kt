operator fun String.get(start:Int, end:Int):String {
    val actualStart = if(start >= 0) start else this.length-start
    val actualEnd = if(end >= 0) end else this.length-end
    return this.substring(actualStart, actualEnd)
}

fun String.ket(index:Int):String {
    val actualIndex = if(index >= 0) index else this.length-index
    return this[actualIndex].toString()
}