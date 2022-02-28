package features

import helpers.CheckChunk
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.plugin.Plugin
import java.util.logging.Level


class LimitBlocksPerChunk(var plugin: Plugin) : Listener {
    var lastCoords = IntArray(2)

    @EventHandler(priority = EventPriority.LOW)
    fun onBlockPlaced(e: BlockPlaceEvent) {
        if (!e.isCancelled) {
            val chunk = e.blockAgainst.chunk
            val block: Block = e.block
            if (CheckChunk.redstoneMaterials.contains(block.type)) {
                val loc: Location = block.location
                val redstoneCount: Int = CheckChunk.redstoneSlice(chunk, loc)
                if (redstoneCount > plugin.config.getInt("limitBlocksPerChunk.maxRedstoneDustPerSlice")) {
                    e.isCancelled = true
                    val x: Int = loc.getBlockX()
                    val y: Int = loc.getBlockY()
                    val z: Int = loc.getBlockZ()
                    val currentX = (x / 300)
                    val currentZ = (z / 300)
                    if (lastCoords[0] != currentX && lastCoords[1] != currentZ) {
                        plugin.logger.log(
                            Level.INFO, String.format(
                                "Redstone place canceled at %d %d %d in %s by %s (%d redstone parts found)",
                                x, y, z,
                                loc.world.name,
                                e.player.name,
                                redstoneCount
                            )
                        )
                        lastCoords[0] = currentX
                        lastCoords[1] = currentZ
                    }
                }
            }
        }
    }
}