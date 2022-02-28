package features

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.plugin.Plugin


class ExplicitBedSpawn(var plugin: Plugin) : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerInteract(e: PlayerBedEnterEvent) {
        if (plugin.config.getBoolean("explicitSpawnSet") && e.player.world.name == "world") {
            e.player.bedSpawnLocation = e.player.location
            e.player.sendMessage("§6Точка спавна установлена")
        }
    }
}