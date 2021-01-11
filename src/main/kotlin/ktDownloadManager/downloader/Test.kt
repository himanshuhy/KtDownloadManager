package ktDownloadManager.downloader

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.deserializers.ByteArrayDeserializer
import com.github.kittinunf.fuel.coroutines.awaitByteArray
import com.github.kittinunf.fuel.coroutines.awaitResponseResult
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

val Accept_Ranges: String = "Accept-Ranges"
val Content_Length: String = "Content-Length"
val Bytes: String = "bytes"
val Range: String = "Range"
val PARALLELISM: Int = 16 //Number of parallel get calls for multi part request

fun main() {
    val url = "http://localhost:"
    val port = 3000
    val path = url + port
    runBlocking {
        Fuel.head(path).awaitResponseResult(ByteArrayDeserializer())
        val HResponse = Fuel.head(path)
                .response { request, response, result ->
                    if (response.get(Accept_Ranges).isNotEmpty()) {
                        val get = response.get(Content_Length)
                        val length = get.first().toInt()
                        val retList = mutableListOf<Deferred<Path>>()
                        for (i in 1..PARALLELISM) {
                            val async: Deferred<Path> = async {
                                Files.write(Paths.get("test-$i"), download(path, i, length))
                            }
                            retList.add(async)
                        }
                        println("What?")
                    } else {
                        //Fetch the entire file in one go
                    }
                }
        HResponse.join()

        //cleanup -> delete the temp files? Actually we can ignore them as the same files will be overwritten next time we download something new. Perhaps better to create files in temp folder
    }
}

suspend fun download(path: String, i: Int, length: Int): ByteArray {
    val parts = length / PARALLELISM
    val start = (i - 1) * parts
    var end = parts * i - 1
    if (i == PARALLELISM)
        end = length
    return Fuel.get(path).appendHeader(Range, "$Bytes=$start-$end").awaitByteArray()
}