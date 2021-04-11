import java.io.File

fun main() {
    File("C:\\Users\\shmue\\OneDrive\\Desktop\\td pictures\\pics").listFiles().forEach{it.renameTo(File(it.absolutePath.removeSuffix(it.name)+"c${it.name}"))}
}