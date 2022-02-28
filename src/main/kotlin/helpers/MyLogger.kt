package helpers

import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.sql.Timestamp

class MyLogger(var plugin: Plugin) {
    var logsfolder: File = File(plugin.dataFolder, "logs")

    fun setupLogFolder() {
        if (plugin.dataFolder?.exists()!!) { // Check if plugin folder exists
            plugin.dataFolder.mkdir() // if not then create it
        }
        if (!logsfolder.exists()) { // Check if logs folder exists
            logsfolder.mkdirs() // if not then create it
            plugin.server.consoleSender
                .sendMessage(ChatColor.GREEN.toString() + "Created the logs folder") // Send a message to console that the folder has been created
        }
    }

    fun logToFile(file: String, message: String?) {
        try {
            val dataFolder: File = plugin.dataFolder // Sets file to the plugins/<pluginname> folder
            if (!dataFolder.exists()) { // Check if logs folder exists
                dataFolder.mkdir() // if not then create it
            }
            val saveTo: File = File(plugin.dataFolder.toString() + "/logs/", "$file.log") // Sets the path of the new log file
            if (!saveTo.exists()) { // Check if logs folder exists
                saveTo.createNewFile() // if not then create it
            }
            val fw = FileWriter(saveTo, true) // Create a FileWriter to edit the file
            val pw = PrintWriter(fw) // Create a PrintWriter
            val timestamp = Timestamp(System.currentTimeMillis())
            pw.println(
                java.lang.String.format(
                    "[%s] %s",
                    timestamp,
                    message
                )
            ) // This is the text/message you will be writing to the file
            pw.flush()
            pw.close()
        } catch (e: IOException) {
            e.printStackTrace() // If there's any errors in this process it will print the error in console
        }
    }
}