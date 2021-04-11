

import kotlinx.coroutines.*
import java.io.File
import java.net.URL
import kotlin.system.measureNanoTime

@ObsoleteCoroutinesApi
@Suppress("Unused")
fun main() {
    val mode = 2
    /*launch { // context of the parent, main runBlocking coroutine
        println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
        println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(Dispatchers.Default) { // will get dispatched to DefaultDispatcher
        println("Default               : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
        println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
    }*/
    if(mode==1) try {

        val listOfShiurim = mutableListOf<ShiurFullPage>()
        var counter = 0
        val split = getJsonAndWriteFileIfNonexistant().split("},")
        val size = split.size
        println("Start")
        /*val a =*/ split.forEach {
            GlobalScope.launch(newFixedThreadPoolContext(7,"MyOwnThread")) {
                withTimeout(10000L) {
                    if (isActive) listOfShiurim.add(parseShiurFromJSON(it))
                    println("Parsed1: ${counter++}/$size")
                }
            }
        }

//    runBlocking {
//        a.forEach{it.await()}
//    }

        /* val file = File("C:\\Users\\shmue\\public-android\\app\\src\\main\\res\\raw\\sample_shiurim.txt")
    if(file.exists()) file.delete()
    file.createNewFile()
    val writer = file.bufferedWriter()
    listOfShiurim.forEach{
        writer.appendLine("ShiurFullPage(${it.id}, title=${it.title}, length=${it.length}, speaker=${it.speaker}, category = ${it.category}, series = ${it.series})")
    }
    writer.close()*/
    }catch(e:TimeoutCancellationException){
        println("thrown 1")
    }
    println("Time: "+measureNanoTime{
        val listOfShiurim = mutableListOf<ShiurFullPage?>()
        var counter = 0
        val split = getJsonAndWriteFileIfNonexistant().split("(?=\\{\"ShiurID\":)".toRegex())
        val size = split.size
        println("Start")
        //This is a faster coroutine by about 40%-50%
        val a = split.map {
            GlobalScope.async(newFixedThreadPoolContext(7, "MyOwnThread")) {
                listOfShiurim.add(parseShiurFromJSON(it))
                println("Parsed2: ${counter++}/$size")
            }
        }
        println("Start 1")
        runBlocking {
            a.forEach { it.join() }
        }

        File("results.txt").apply {
            if (exists()) delete()
            createNewFile()
            val bufferedWriter = bufferedWriter()
            listOfShiurim
                .map {
                    "" +
                            "${it?.
                            id.
                            toString()};;" +
                            "${it?.title.toString()};;" +
                            "${it?.length.toString()};;" +
                            "${it?.speaker.toString()};;" +
                            "${it?.category.toString()}" +
                            if (it?.series.toString() != "")
                                ";;${it?.series.toString()}"
                            else ""
                }
                .forEach {
                    println("Written: $it")
                    bufferedWriter.appendLine(it)
                }
        }
    })
}
private fun parseShiurFromJSON(json: String): ShiurFullPage {
    /*{"ShiurID":"24265","Title":"Judaism Is Not a Chore","Length":"3267","FormattedLength":"54","SpeakerID":"14","Speaker":"Rabbi Mordechai Becher","SpeakerSE":"rabbi-mordechai-becher"}*/
    val id = "(?<=\"ShiurID\":\")\\d+(?=\")".toRegex().find(json)?.value ?: "24265"
    val title =
        "(?<=\"Title\":\")[\\w\\s-().'-,;&]+(?=\")".toRegex().find(json)?.value ?: "TEST: Judaism Is Not a Chore"
    val length = "(?<=\"Length\":\")\\d+(?=\")".toRegex().find(json)?.value ?: "TEST: 3267"
    val speaker = "(?<=\"Speaker\":\")[\\w\\s]+(?=\")".toRegex().find(json)?.value
        ?: "TEST: Rabbi Mordechai Becher"
    val shiurFullPage = URL("http://torahdownloads.com/shiur-$id.json").readText() /*""*/
    val categoryLink = "(?<=\"category\":\"\\\\/)[\\w-.]+(?=\")".toRegex().find(shiurFullPage)?.value?.replace("html","json")
        ?: "c-2-halacha.json"
//    println("json: $json")
    val categoryPage = URL("http://torahdownloads.com/$categoryLink").readText() /*""*/
    val categoryName = "(?<=\"name\":\")[\\w\\s-]+(?=\")".toRegex().find(categoryPage)?.value ?: "TEST: "
    val series = "(?<=\"quickseries_name\":\")[\\w\\s-().'-,;]*(?=\")".toRegex().find(shiurFullPage)?.value ?: ""
//    if(series!="") println("ShiurFullPage(id=$id, title=$title, length=$length, speaker= $speaker, category=$categoryName, series=$series)")
    return ShiurFullPage(id, title=title, length=length, speaker=speaker, category = categoryName, series = series)
}
/*C:\Users\shmue\public-android\app\src\main\res\raw\list_of_speakers_page.json*/
/*"description":"*/
/*"},"data"*/

private fun getJsonAndWriteFileIfNonexistant(baseURL:String = "http://torahdownloads.com/c-3-machshava.json", pages:IntRange? = 2..128, filename:String = "C:\\Users\\shmue\\public-android\\app\\src\\main\\res\\raw\\machshava_category_full_json.json", overwrite:Boolean = false): StringBuilder {
    val stringBuilder = StringBuilder()
    val x = File(filename)
    if(x.exists()) {
        if(overwrite) {
            x.delete()
            x.createNewFile()
            stringBuilder.append(URL(baseURL).readText().run{substring(indexOf(""""shiurim":{"list":[{"""),indexOf("""}],"page":"""))})
            if(pages!=null) for (i in pages) {
                println("URL: $i/${pages.last}")
                stringBuilder.append(URL("$baseURL?page=$i").readText().run{substring(indexOf(""""shiurim":{"list":[{"""),indexOf("""}],"page":"""))})
            }
            val y = x.bufferedWriter()
            y.append(stringBuilder)
            y.close()
        }
        else{
            val y = x.bufferedReader()
            stringBuilder.append(y.readText())
            y.close()
        }
    } else {
        x.createNewFile()
        stringBuilder.append(URL(baseURL).readText().run{substring(indexOf(""""shiurim":{"list":[{"""),indexOf("""}],"page":"""))})
        if(pages!=null) for (i in pages) {
            println("URL: $i/${pages.last}")
            stringBuilder.append(URL("$baseURL?page=$i").readText().run{substring(indexOf(""""shiurim":{"list":[{"""),indexOf("""}],"page":"""))})
        }
        val y = x.bufferedWriter()
        y.append(stringBuilder)
        y.close()
    }
    return stringBuilder
}
/*
class SecondClass : AppCompatActivity() {
    fun main() {
        val dispatcher = Dispatcher(Executors.newFixedThreadPool(20))
        dispatcher.maxRequests = 20
        dispatcher.maxRequestsPerHost = 1
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectionPool(ConnectionPool(100, 30, TimeUnit.SECONDS))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.x.x/x/".toHttpUrlOrNull() ?: "".toHttpUrl())
            .addConverterFactory(PageAdapter.FACTORY)
            .build()
        val requestAddress = retrofit.create(PageService::class.java)
        val pageCall: Call<Page> = requestAddress["https://www.x.x/x/".toHttpUrlOrNull()]
        pageCall.enqueue(object : Callback<Page?>() {
            fun onResponse(call: Call<Page?>?, response: Response<Page?>) {
                Log.i("ADASDASDASD", response.body().content)
            }

            fun onFailure(call: Call<Page?>?, t: Throwable?) {}
        })
    }

    internal class Page(var content: String)
    internal class PageAdapter : Converter<ResponseBody, Page> {
        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody): Page {
            val document: Document = Jsoup.parse(responseBody.string())
            val value: Element = document.select("script").get(1)
            val content: String = value.html()
            return Page(content)
        }

        companion object {
            val FACTORY: Converter.Factory = object : Converter.Factory() {
                override fun responseBodyConverter(
                    type: Type,
                    annotations: Array<Annotation?>?,
                    retrofit: Retrofit?
                ): Converter<ResponseBody, *>? {
                    return if (type === Page::class.java) PageAdapter() else null
                }
            }
        }
    }

    internal interface PageService {
        @GET
        operator fun get(@Url url: HttpUrl?): Call<Page?>?
    }
}*/
fun <T> T.println() = println(this)
fun <T> T.println(returnInstance: Boolean) = this.apply { println(this) }


public inline fun <T> List<T>.myforEach(action: (T) -> Unit): List<T> {
    for (element in this) action(element)
    Generic(String::class.java).checkType("")
    return this
}
class Generic<T : Any>(val klass: Class<T>) {
    companion object {
        inline operator fun <reified T : Any>invoke() = Generic(T::class.java)
    }

    fun checkType(t: Any) {
        when {
            klass.isAssignableFrom(t.javaClass) -> println("Correct type")
            else -> println("Wrong type")
        }

    }
}
data class ShiurFullPage(
    val id: String? = "1008064",
    val page_title: String? = "Chinuch - Shiur 1 - Rabbi Yisroel Belsky - TD1008064",
    val title: String? = "Chinuch - Shiur 1",
    val speaker/*Rabbi Yisroel Belsky*/: String? = "Rabbi Yisroel Belsky",
    val speaker_image: String? = "assets\\/speakers\\/64.jpg",
    val length: String? = "83",
    val links: List<String>? = listOf(
        "shiur-1008064-download.mp3",
        "\\/c-223-chinuch-parenting.html",
        "\\/s-64-rabbi-yisroel-belsky.html"
/*
        val download: String = "shiur-1008064-download.mp3",
        val category: String = "\\/c-223-chinuch-parenting.html",
        val speaker*//*\/s-64-rabbi-yisroel-belsky.html" - just represented as "speaker" in actual JSON*//*
: String = "\\/s-64-rabbi-yisroel-belsky.html",
*/
    ),
    val category: String? = "\\/c-223-chinuch-parenting.html",//is redundant with the links list, but being kept here until better solution
    val attachment: String? = "",
    val description: String? = "",
    val source: String? = "",
    val attachment_name: String? = "",
    val uploaded: String? = "February 5, 2020",
    val language: String? = "",
    val series: String? = "C",
    val quickseries: String? = "",
    val quickseries_name: String? = ""
)
