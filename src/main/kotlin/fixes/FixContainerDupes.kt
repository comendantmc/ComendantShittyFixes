package fixes

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent
import org.bukkit.Location
import org.bukkit.entity.ChestedHorse
import org.bukkit.entity.EntityType
import org.bukkit.entity.Minecart
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector
import java.util.logging.Level


class FixContainerDupes(val plugin: Plugin) : Listener {
    private fun printLocation(loc: Location?, msg: String?) {
        if (loc != null) plugin.logger.log(
            Level.INFO, java.lang.String.format(
                "%s (%.00f %.00f %.00f)",
                msg,
                loc.x,
                loc.y,
                loc.z
            )
        ) else plugin.logger.log(Level.INFO, msg)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryMove(e: InventoryClickEvent) {
        if (!plugin.config.getBoolean("dupes.preventContainerDupe.enabled") || e.isCancelled) return
        val clickedInv = e.clickedInventory
        val inv = e.inventory
        if (clickedInv == null || inv == null) return
        val isMinecart = clickedInv.holder is Minecart || inv.holder is Minecart
        val isHorse = clickedInv.holder is ChestedHorse || inv.holder is ChestedHorse
        if (isHorse
            && plugin.config.getBoolean("dupes.preventContainerDupe.horses")
            && e.currentItem.type.toString() !== "CARPET" && e.currentItem.type.toString() !== "SADDLE"
        ) {
            e.isCancelled = true
            printLocation(clickedInv.location, "prevent moving items to a forbidden horse slot")
        }
        if (isMinecart
            && plugin.config.getBoolean("dupes.preventContainerDupe.minecarts")
            && (inv.location.distance(clickedInv.location) > 5 || !(inv.location.chunk.isLoaded && clickedInv.location.chunk.isLoaded)
                    || (e.whoClicked as Player).walkSpeed < 0.2)
        ) {
            e.isCancelled = true
            printLocation(clickedInv.location, "prevent moving items to a forbidden container")
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onHopperPickup(e: InventoryPickupItemEvent) {
        if (!plugin.config.getBoolean("dupes.preventContainerDupe.enabled")
            || e.isCancelled || !plugin.config.getBoolean("dupes.preventContainerDupe.hopperMinecarts")
        )
            return

        if (e.inventory.holder is Minecart) {
            e.isCancelled = true
            val loc: Location = e.item.location
            plugin.logger.log(
                Level.INFO, java.lang.String.format(
                    "interacted with hopper minecart (%.00f %.00f %.00f)",
                    loc.x,
                    loc.y,
                    loc.z
                )
            )
            if (plugin.config.getBoolean("dupes.hopperMinecartRemoveItems")) {
                e.item.remove()
            } else {
                val dir: Vector = loc.direction
                val vec = Vector(dir.x * 0.8, dir.y * 0.8, dir.z * 0.8)
                e.item.velocity = vec
            }
        }
    }

    @EventHandler
    fun onChestedDeath(e: EntityDeathEvent) {
        if (e.entity is ChestedHorse && plugin.config.getBoolean("dupes.preventDeathOfAnimalsWithChests")) {
            (e.entity as ChestedHorse).inventory.clear()
            (e.entity as ChestedHorse).isCarryingChest = false
            e.entity.remove()
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onChestedSpawn(e: PreCreatureSpawnEvent) {
        if (plugin.config.getBoolean("dupes.preventAnimalsWithChests") && (e.type == EntityType.DONKEY || e.type == EntityType.LLAMA || e.type == EntityType.MULE)) e.setShouldAbortSpawn(
            true
        )
    }

    @EventHandler
    fun onChestedInteract(e: PlayerInteractEntityEvent) {
        if (!plugin.config.getBoolean("dupes.clearInvInteractAnimalsWithChests") || e.rightClicked !is ChestedHorse)
            return
        val horse = e.rightClicked as ChestedHorse
        val saddle = horse.inventory.saddle
        horse.inventory.clear()
        horse.isCarryingChest = false
        horse.inventory.saddle = saddle
        if (plugin.config.getBoolean("dupes.cancelInteractAnimalsWithChests"))
            e.isCancelled = true
    }
}