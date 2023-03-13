package fixes

import org.bukkit.Material
import org.bukkit.entity.EnderCrystal
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
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

    @EventHandler
    private fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (!plugin.config.getBoolean("silentSwitchFix")) return
        val player = event.player
        val inventory = player.inventory
        val mainHandItem: ItemStack = inventory.itemInMainHand
        val offHandItem: ItemStack = inventory.itemInOffHand

        if (event.action.name == "RIGHT_CLICK_BLOCK" || (event.clickedBlock.type == Material.BEDROCK || event.clickedBlock.type == Material.OBSIDIAN)) {
            if (!((mainHandItem.type == Material.END_CRYSTAL) ||
                (offHandItem.type == Material.END_CRYSTAL))) {
                event.isCancelled = true
            }
        }
    }
}