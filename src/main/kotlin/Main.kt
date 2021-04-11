import java.io.File

class Main {
    init{
        val outputFile = File("Result.txt").also{it.createNewFile()}
        val writer = outputFile.bufferedWriter()
        val pathname = System.getProperty("user.dir")
        println("User dir: $pathname")
        val filesInDir = File(pathname).listFiles()!!
        val filesAsString = filesInDir.map{it.nameWithoutExtension}
        val filesAlreadyWritten = mutableListOf<String>()
        filesAsString.printEachOnNewLine()
        for(file in filesInDir){
            if(filesAsString.count{it==file.nameWithoutExtension}==2 && file.nameWithoutExtension !in filesAlreadyWritten) {
                println("$file appears twice")
                filesAlreadyWritten.add(file.nameWithoutExtension)
                writer.appendLine("${file.nameWithoutExtension}.mp3~~${file.nameWithoutExtension}.pdf")
            }
        }
        writer.close()
        val shiurId = getShiurID()
        val attachmentFileName = getAttachmentFileName()
        val query = "INSERT INTO `tl_admin`.`al_Attachments` (`ShiurID`, `Name`, `StorageTypeID`, `Path`, `FileFormatID`) VALUES ('$shiurId', 'Shiur Handout', '2', '$attachmentFileName', '0');R"
    }
}
private fun getShiurID() = "23330"
private fun getAttachmentFileName() = "test.pdf"
private fun <T> Iterable<T>.printEachOnNewLine() {
    for(element in this) println(element)
}
