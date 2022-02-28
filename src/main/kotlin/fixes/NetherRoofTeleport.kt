package fixes

import interfaces.RepeatingTask
import org.bukkit.Location
import org.bukkit.plugin.Plugin


class NetherRoofTeleport(val plugin: Plugin) : RepeatingTask {
    override val delay: Long = 100
    override val period: Long = 60

    override fun run() {
        for (player in plugin.server.onlinePlayers) {
            if (player.world.name.equals("world_nether", ignoreCase = true) && player.location.blockY >= 128) {
                val loc: Location = player.location
                loc.y = 120.0
                player.teleport(loc)
            }
        }
    }
}