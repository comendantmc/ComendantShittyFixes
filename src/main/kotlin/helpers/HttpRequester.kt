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
    @Throws(Exception::class)
    fun sendPost(urlString: String?, data: String?): String? {
        try {
            val url = URL(urlString)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("method", "POST")
            conn.setDoOutput(true)
            val writer = OutputStreamWriter(conn.getOutputStream())
            writer.write(data)
            writer.flush()
            val `is`: InputStream = BufferedInputStream(conn.getInputStream())
            val contents = ByteArray(1024)
            var bytesRead = 0
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
            val `is`: InputStream = BufferedInputStream(conn.getInputStream())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}