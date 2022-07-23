import helpers.CheckChunk
import helpers.HttpRequester
import helpers.TicksApproximation
import helpers.Translit
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.Vehicle
import org.bukkit.entity.Wither
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Level
import java.util.stream.Collectors


class Commands(var plugin: Plugin) : interfaces.CustomCommand {
    val prefix: String = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("pluginPrefix"))
    override val command = "csfixes"

    override fun onCommand(
        commandSender: CommandSender,
        command: Command?,
        commandLabel: String?,
        args: Array<String>
    ): Boolean {
        if (!commandSender.hasPermission("anarchy_bundle.admin")) {
            commandSender.sendMessage(
                java.lang.String.format(
                    "%s%sNot allowed",
                    prefix,
                    ChatColor.RED
                )
            )
            return true
        }

        if (args[0].equals("reload", ignoreCase = true)) {
            plugin.reloadConfig()
            commandSender.sendMessage(
                java.lang.String.format(
                    "%s%sConfig reloaded",
                    prefix,
                    ChatColor.GREEN
                )
            )
            return true
        } else if (args[0].equals("coordinates", ignoreCase = true)) {
            val msg: String = plugin.server.onlinePlayers.stream().map { player ->
                val loc: Location = player.location
                java.lang.String.format("%s: %.1f %.1f %.1f", player.name, loc.x, loc.y, loc.z)
            }.collect(Collectors.joining(String.format("%n")))
            if (plugin.config.getBoolean("telegram.enabled")) {
                val httpRequester = HttpRequester()
                try {
                    val url = java.lang.String.format(
                        "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                        plugin.config.getString("telegram.secret"),
                        plugin.config.getString("telegram.chatId"),
                        URLEncoder.encode(msg, "UTF8")
                    )
                    httpRequester.fetch(url)
                } catch (ex: Exception) {
                    plugin.logger.log(Level.WARNING, "Failed telegram webhook request")
                }
            }
            commandSender.sendMessage(
                java.lang.String.format(
                    "%s%n%s%sSuccessfully sent coordinates",
                    msg,
                    prefix,
                    ChatColor.GREEN
                )
            )
            return true
        } else if (args[0].equals("coords", ignoreCase = true)) {
            val msg: String = plugin.server.onlinePlayers.stream().map { player ->
                val loc: Location = player.location
                java.lang.String.format("%s: %.1f %.1f %.1f", player.name, loc.x, loc.y, loc.z)
            }.collect(Collectors.joining(String.format(", ")))
            commandSender.sendMessage(msg)
            return true
        } else if (args[0].equals("report", ignoreCase = true)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, object : BukkitRunnable() {
                override fun run() {
                    val dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                    val now = LocalDateTime.now()
                    var out = String.format(
                        "Chunk report from %s, players online: %d, TPS: %.2f%n===%n",
                        dtf.format(now),
                        Bukkit.getOnlinePlayers().size,
                        Bukkit.getTPS()[0]
                    )
                    for (w in Bukkit.getWorlds()) {
                        for (c in w.loadedChunks) {
                            val entities = c.entities.size
                            var players = 0
                            var vehicles = 0
                            for (entity in c.entities) {
                                if (entity is Vehicle) {
                                    vehicles++
                                } else if (entity is Player) {
                                    players++
                                }
                            }
                            val redstone: Int = CheckChunk.full(
                                CheckChunk.redstoneMaterials,
                                c.getChunkSnapshot(false, false, false)
                            )
                            if ((entities < 10 || vehicles > 5) && players == 0 && vehicles == 0 && redstone == 0) continue
                            out += String.format(
                                "Chunk (%s, %d 80 %d) has %d entities (%d players, %d vehicles), %d redstone structures%n",
                                w.name,
                                c.x * 16,
                                c.z * 16,
                                entities,
                                players,
                                vehicles,
                                redstone
                            )
                        }
                    }
                    val httpRequester = HttpRequester()
                    try {
                        val providerConf: String = plugin.config.getString("hastebin.provider")
                        val provider = providerConf.ifEmpty { "https://www.toptal.com/developers/hastebin" }
                        val res = httpRequester.sendPost("$provider/documents", out)
                        val obj: Any = JSONParser().parse(res)
                        val jo = obj as JSONObject
                        if (jo["key"] != null) {
                            commandSender.sendMessage(
                                java.lang.String.format(
                                    "%s%sReport is ready at this link %s/%s",
                                    prefix,
                                    ChatColor.GREEN,
                                    provider,
                                    jo["key"]
                                )
                            )
                        } else {
                            commandSender.sendMessage(
                                java.lang.String.format(
                                    "%s%sError generation report: %s",
                                    prefix,
                                    ChatColor.RED,
                                    res
                                )
                            )
                        }
                    } catch (ex: Exception) {
                        plugin.logger.log(Level.WARNING, "Failed hastebin request")
                    }
                }
            })
            return true
        } else if (args[0].equals("gamemodes", ignoreCase = true)) {
            val players: Collection<Player> = plugin.server.onlinePlayers
            val gamemodes = intArrayOf(0, 0, 0, 0)
            for (player in players) {
                gamemodes[player.gameMode.value]++
            }
            commandSender.sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                "${prefix}&aGamemodes: survival[${gamemodes[0]}], creative[${gamemodes[1]}], adventure[${gamemodes[2]}, spectator[${gamemodes[3]}"
                )
            )
            return true
        } else if (args[0].equals("withers", ignoreCase = true)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, object : BukkitRunnable() {
                override fun run() {
                    val names =
                        if (plugin.config.getBoolean("characterFilter.replaceAllToCyrillicOnNameTags"))
                            Bukkit.getOfflinePlayers().map { it.name }
                        else emptyList()
                    val regex = plugin.config.getString("characterFilter.nameTagRegex")
                    val blacklist = plugin.config.getStringList("characterFilter.nameTagBlacklist")
                    val withers = mutableListOf<String>()
                    for (world in Bukkit.getWorlds()) {
                        for (entity in world.entities) {
                            if (entity is Wither && entity.customName != null) {
                                val lived = TicksApproximation.ticksInDays(entity.ticksLived, plugin.config.getDouble("meta.averageTPS"))
                                val loc = entity.location
                                withers += "'&6${entity.customName}&r' &b[&r${loc.blockX} ${loc.blockY} ${loc.blockZ}, ${world.name}, lived ${
                                    "%.4f".format(lived)
                                }&b]&r"
                                if (lived > plugin.config.getDouble("characterFilter.nameTagMinLived"))
                                    continue

                                var name = entity.customName

                                if (blacklist.isNotEmpty())
                                    name = name.replace(blacklist.joinToString("|").toRegex(RegexOption.IGNORE_CASE), "")

                                name = name.replace(regex.toRegex(), "").trim()

                                if (plugin.config.getBoolean("characterFilter.replaceAllToCyrillicOnNameTags"))
                                    name = if (plugin.config.getBoolean("characterFilter.whitelistNicknames")) {
                                        Translit.translitPerWordWithWhitelist(name, names)
                                    } else {
                                        Translit.translit(name)
                                    }

                                if (name.isEmpty()) {
                                    entity.health = 0.0
                                    continue
                                }
                                entity.customName = name
                            }
                        }
                    }
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "Withers: ${withers.joinToString(", ")}"))
                }
            })
            return true
        }
        commandSender.sendMessage(java.lang.String.format("%s%sNothing to do", prefix, ChatColor.RED))
        return true
    }
}