import java.io.File

fun main() {
/*
    val list = mutableListOf<String>()
    File("results.txt")
        .bufferedReader()
        .apply {
            readLines()
                .forEach {
                    if (it
                            .split(";;")
                            .count() == 6
                    ) {
                        list.add(it)
                        println(it)
                    }
                }
            close()
        }
*/
    println(RandomTest3.HELLO)
    println(RandomTest3.HELLO.name)
    println(RandomTest3.HELLO.ordinal)
    println(RandomTest3.HELLO.declaringClass)
    println(RandomTest3.HELLO.javaClass)
    println(RandomTest3.HELLO.toString())
    val c = (c() as a)
    println(c.javaClass)
}
enum class RandomTest3 {
    HELLO,
    ME
}
open class a
class b:a()
class c:a()