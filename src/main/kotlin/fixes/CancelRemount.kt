package fixes

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.plugin.Plugin


class CancelRemount(val plugin: Plugin) : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onMount(e: PlayerInteractEntityEvent) {
        if (plugin.config.getBoolean("dupes.cancelInteractFromVehicle") && e.player.isInsideVehicle)
            e.isCancelled = true
    }
}