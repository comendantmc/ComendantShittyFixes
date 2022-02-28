package fixes

import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import java.util.logging.Level


class ResetSpeedAttributes(val plugin: Plugin) : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (!plugin.config.getBoolean("resetPlayerSpeeds"))
            return

        val player = e.player
        val walkSpeed = player.walkSpeed
        if (walkSpeed < 0.2) {
            if (walkSpeed < 0.1) {
                plugin.logger.log(Level.INFO, String.format("%s's walkspeed: %f", player.name, walkSpeed))
            }
            player.flySpeed = 0.05f
            player.walkSpeed = 0.2f
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).baseValue = 0.2
        }
    }
}