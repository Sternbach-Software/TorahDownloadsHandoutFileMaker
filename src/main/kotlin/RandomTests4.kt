import java.io.File
import java.net.URL

fun main() {
    getJsonAndWriteFileIfNonexistant()
}
private fun getJsonAndWriteFileIfNonexistant(
    baseURL: String = "http://torahdownloads.com/",
    filename: String = "C:\\Users\\shmue\\public-android\\app\\src\\main\\res\\raw\\list_of_speakers_page.json"
): StringBuilder {
    val stringBuilder = StringBuilder()
    val x = File(filename)
    val q = x.readLines()
    val links = mutableListOf<String>()
    var counter1 = 0
    var counter2 = 0
    val n = q.size
    q.forEach{
        println("Line ${counter1++}/$n")
        if(it.contains(""""link":""")) links.add(baseURL + it.substring(it.indexOf('s'),it.indexOf(".html")) +".json")}
    val o = links.size
    links.forEach{
        println("Link ${counter2++}/$o")
        stringBuilder.appendLine(
            URL(it).readText().run { substring(indexOf("""""description":"""")+1, indexOf(""""},"data"""")) })}
    val y = File("Results22").apply{createNewFile()}.bufferedWriter()
    y.append(stringBuilder)
    y.close()
    return stringBuilder
}