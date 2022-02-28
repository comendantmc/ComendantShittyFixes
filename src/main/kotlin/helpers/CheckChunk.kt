package helpers

import com.google.common.collect.Sets
import org.bukkit.Chunk
import org.bukkit.ChunkSnapshot
import org.bukkit.Location
import org.bukkit.Material


object CheckChunk {
    fun full(materials: Set<Material?>, c: ChunkSnapshot): Int {
        var count = 0
        for (x in 0..15) {
            for (z in 0..15) {
                for (y in 0..255) {
                    val mat = c.getBlockType(x, y, z)
                    if (materials.contains(mat)) {
                        count++
                    }
                }
            }
        }
        return count
    }

    var redstoneMaterials: Set<Material> = Sets.newHashSet(
        Material.DIODE_BLOCK_OFF,
        Material.REDSTONE_WIRE,
        Material.REDSTONE_BLOCK,
        Material.REDSTONE_COMPARATOR,
        Material.DIODE_BLOCK_ON,
        Material.REDSTONE_TORCH_ON,
        Material.REDSTONE_TORCH_OFF,
        Material.DIODE,
        Material.HOPPER,
        Material.OBSERVER,
        Material.PISTON_STICKY_BASE,
        Material.PISTON_BASE,
        Material.SLIME_BLOCK
    )

    fun redstoneSlice(c: Chunk, loc: Location): Int {
        var count = 0
        val cx = c.x shl 4
        val cz = c.z shl 4
        val iy: Int = loc.blockY
        for (x in cx until cx + 16) {
            for (z in cz until cz + 16) {
                val start = if (iy > 4) iy - 4 else 0
                val end = if (iy < 250) iy + 5 else 256
                for (y in start..end) {
                    if (redstoneMaterials.contains(c.getBlock(x, y, z).type)) count++
                }
            }
        }
        return count
    }
}