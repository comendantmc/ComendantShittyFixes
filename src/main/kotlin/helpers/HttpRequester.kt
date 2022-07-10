package helpers

import org.bukkit.Bukkit
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection


class HttpRequester {
    var urlWhiteList = arrayOf("api.telegram.org")
    @Throws(Exception::class)
    fun sendPost(urlString: String, data: String): String {
        try {
            val url = URL(urlString)
            val remoteHost: String = url.host
            if (!urlWhiteList.contains(remoteHost)) throw IOException()
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("method", "POST")
            conn.doOutput = true
            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(data)
            writer.flush()
            val `is`: InputStream = BufferedInputStream(conn.inputStream)
            val contents = ByteArray(1024)
            var bytesRead: Int
            var resString = ""
            while (`is`.read(contents).also { bytesRead = it } != -1) {
                resString += String(contents, 0, bytesRead)
            }
            return resString
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    fun fetch(urlString: String?) {
        Bukkit.getLogger().info("HttpRequester.fetch")
        try {
            val url = URL(urlString)
            val conn: URLConnection = url.openConnection()
            conn.setRequestProperty("method", "GET")
            BufferedInputStream(conn.inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
