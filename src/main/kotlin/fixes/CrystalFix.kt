package fixes

import org.bukkit.entity.EnderCrystal
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.plugin.Plugin


class CrystalFix(val plugin: Plugin) {
    @EventHandler
    private fun onEntityDamageEntity(event: EntityDamageByEntityEvent) {
        if (event.entity is EnderCrystal
            && event.damager is Player
            && event.entity.ticksLived <= plugin.config.getInt("crystal.minLived")
        ) {
            event.isCancelled = true
        }
    }
}