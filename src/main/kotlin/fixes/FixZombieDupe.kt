package fixes

import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.plugin.Plugin
import java.util.logging.Level


class FixZombieDupe(val plugin: Plugin) : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityPickup(e: EntityPickupItemEvent) {
        if (!plugin.config.getBoolean("dupes.killZombiesWithItems") || e.isCancelled)
            return
        if (e.entity is Zombie) {
            e.isCancelled = true
            e.entity.equipment.clear()
            e.entity.damage(1000.0)
            plugin.logger.log(
                Level.INFO, String.format(
                    "pickup canceled (%.00f %.00f %.00f)",
                    e.entity.location.x,
                    e.entity.location.y,
                    e.entity.location.z
                )
            )
        }
    }
}