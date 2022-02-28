package features

import helpers.HttpRequester
import interfaces.RepeatingTask
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.Plugin
import java.net.URLEncoder
import java.util.UUID
import java.util.logging.Level


class StrictPositionCheck(var plugin: Plugin) : RepeatingTask, Listener {
    private var httpRequester: HttpRequester = HttpRequester()
    private val playerLocations: MutableMap<UUID, Location> = HashMap<UUID, Location>()

    override val delay: Long = 100
    override val period: Long = 300

    override fun run() {
        if (!plugin.config.getBoolean("strictPositionCheck.enable")) return
        for (player in plugin.server.onlinePlayers) {
            val uuid = player.uniqueId
            if (player.gameMode == GameMode.SURVIVAL && playerLocations.containsKey(uuid)) {
                val oldLoc: Location? = playerLocations[uuid]
                val newLoc: Location = player.location
                if (oldLoc != null && oldLoc.world.equals(newLoc.world)) {
                    val distance: Double = newLoc.distance(oldLoc)
                    val minimalSpawnDistance: Int =
                        plugin.config.getInt("strictPositionCheck.minimalSpawnDistance")
                    val reportedDistance: Double = plugin.config.getDouble("strictPositionCheck.reportedDistance")
                    if (distance > reportedDistance && (newLoc.world.name
                            .equals("world_nether", ignoreCase = true) || newLoc.blockX > minimalSpawnDistance || newLoc.blockZ > minimalSpawnDistance)
                    ) {
                        if (plugin.config.getBoolean("strictPositionCheck.teleportBack")) {
                            player.teleport(oldLoc)
                        }
                        //player.damage(plugin.getConfig().getDouble("strictPositionCheck.damage"));
                        val loc: Location = player.location
                        val vehicle: Entity = player.vehicle
                        val msg = java.lang.String.format(
                            "%s was really fast: %.1f blocks (%s, %d %d %d) ridding %s",
                            player.name,
                            distance,
                            loc.world.name,
                            loc.blockX,
                            loc.blockY,
                            loc.blockZ,
                            if (vehicle != null) vehicle.name else if (player.isGliding) "elytra" else "nothing"
                        )
                        plugin.logger.log(Level.INFO, msg)
                        if (plugin.getConfig().getBoolean("telegram.enabled")) {
                            try {
                                val url = String.format(
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
                    }
                }
            }
            playerLocations[uuid] = player.location
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerDeath(e: PlayerDeathEvent) {
        playerLocations.remove(e.entity.uniqueId)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerChangedWorld(e: PlayerChangedWorldEvent) {
        playerLocations.remove(e.player.uniqueId)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerLeave(e: PlayerQuitEvent) {
        playerLocations.remove(e.player.uniqueId)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerTeleport(e: PlayerTeleportEvent) {
        playerLocations.remove(e.player.uniqueId)
    }
}