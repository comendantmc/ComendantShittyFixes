package interfaces

import org.bukkit.command.CommandExecutor

interface CustomCommand : CommandExecutor {
    val command: String
}