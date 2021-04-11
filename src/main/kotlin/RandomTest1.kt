import java.io.File
import java.lang.StringBuilder

fun main() {
    val x =   File("C:\\Users\\shmue\\Downloads\\TorahDownloadsHandoutFileMaker\\src\\main\\resources\\random.txt")
    val a = x.readLines()
    val listOfMasechtos = mutableListOf<String>()
    val listOfNumbers = mutableListOf<List<String>>()
    for(i in a){
        val list = i.split(",")
        listOfMasechtos.add(list[0])
        listOfNumbers.add(list.subList(1,list.size))
    }
    println("listOfMasechtos=$listOfMasechtos")
    println("listOfNumbers=$listOfNumbers")
    val stringBuilder = StringBuilder("listOf(")
    for(list in listOfNumbers){
        stringBuilder.append("listOf(")
        for(number in list){
            stringBuilder.append("$number,")
        }
        stringBuilder.append("),")
    }
    stringBuilder.append(")")
    println(stringBuilder)
}