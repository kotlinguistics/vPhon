/**Allows the use of Python-like negative indices, which go backwards from the end of a [String].
 * This function is for selecting substrings by `string.get(0, -1)` or `string[0, -1]`,
 *  rather than the bulkier Kotlin `string[0, string.length-1]`.
 *  Also casts the resultant [Char] to a [String]*/
operator fun String.get(start:Int, end:Int):String {
    val actualStart = if(start >= 0) start else this.length-start
    val actualEnd = if(end >= 0) end else this.length-end
    return this.substring(actualStart, actualEnd)
}
/**Allows the use of Python-like negative indices, which go backwards from the end of a [String].
 * This function is for getting a character from a [String] by `string.ket(-1)`,
 *  rather than the bulkier Kotlin `string[string.length-1]`.
 *  Also casts the resultant [Char] to a [String], for convenience.*/
fun String.ket(index:Int):String {
    val actualIndex = if(index >= 0) index else this.length-index
    return this[actualIndex].toString()
}