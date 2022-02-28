package features

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockRedstoneEvent
import org.bukkit.plugin.Plugin
import java.util.logging.Level


class RedstoneInspector(val plugin: Plugin) {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockPlaced(e: BlockRedstoneEvent?) {
        plugin.logger.log(Level.INFO, "Redstone event")
    }
}