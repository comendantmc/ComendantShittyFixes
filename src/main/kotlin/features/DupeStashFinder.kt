package features

import helpers.MyLogger
import org.bukkit.Location
import org.bukkit.block.ShulkerBox
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta
import org.bukkit.plugin.Plugin
import kotlin.math.floor


class DupeStashFinder(var plugin: Plugin) : Listener {
    var visited = HashSet<String>()
    var mylogger: MyLogger = MyLogger(plugin)

    @EventHandler
    fun catchChestOpen(event: InventoryOpenEvent) {
        if (event.inventory.type == InventoryType.CHEST) {
            val loc: Location = event.player.location
            val chunkLoc =
                String.format("%d.%d", floor(loc.x / 32.0).toInt(), floor(loc.x / 32.0).toInt())
            if (visited.contains(chunkLoc)) return
            val items = event.inventory.contents
            var shulkerCount = 0
            var shulkerItemCount = 0
            for (item in items) {
                if (item == null) continue
                val itemType = item.data.itemType.toString()
                if (itemType.contains("SHULKER_BOX")) {
                    val box = (item.itemMeta as BlockStateMeta).blockState as ShulkerBox
                    val shulkerItems = box.inventory.contents
                        .filter { i: ItemStack? -> i != null && i.amount > 0 }
                    shulkerCount += 1
                    shulkerItemCount += shulkerItems.size
                }
            }
            if (shulkerCount < 3) return
            visited.add(chunkLoc)
            val log = String.format(
                "--> %s opened chest with %d shulkers (%d items) on %d %d %d (%s, r.%s.mcr)",
                event.player.name,
                shulkerCount,
                shulkerItemCount,
                loc.x.toInt(),
                loc.y.toInt(),
                loc.z.toInt(),
                loc.world.name,
                chunkLoc
            )
            mylogger.logToFile("chestAccesses", log)
            plugin.server.consoleSender.sendMessage(log)
        }
    }
}