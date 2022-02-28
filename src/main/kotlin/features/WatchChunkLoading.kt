package features

import helpers.RateLimiter
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.plugin.Plugin
import java.util.logging.Level


class WatchChunkLoading(val plugin: Plugin) : Listener {
    private val rateLimiter = RateLimiter(2, 10000)

    //@EventHandler(priority = EventPriority.HIGHEST)
    //public void onPlayerLogin(PlayerJoinEvent e) {
    //    locationStack.push(e.getPlayer().getLocation());
    //}

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onChunkLoad(e: ChunkLoadEvent) {
        if (!plugin.config.getBoolean("chunks.checkLoading"))
            return

        val chunk = e.chunk
        val x = chunk.x * 16.0
        val z = chunk.z * 16.0
        val minimal = plugin.config.getInt("chunks.minimalDistanceFromSpawn")
        if (x < minimal && z < minimal) return
        val world = e.world
        val loc = Location(world, x, 100.0, z)
        var notfound = true
        for (pl in plugin.server.onlinePlayers) {
            if (pl.location.world !== world) continue
            if (loc.distance(pl.location) < plugin.config.getInt("chunks.checkDistance")) {
                notfound = false
                break
            }
        }

        //for (int i = 0; i < locationStack.size(); i++) {
        //    Location sloc = locationStack.pop();
        //    if (sloc == null)
        //        break;
        //    if (sloc.getWorld() != world)
        //        continue;
        //    if (loc.distance(sloc) < plugin.getConfig().getInt("chunks.checkDistance")) {
        //        notfound = false;
        //        break;
        //    }
        //}

        if (notfound) {
            if (this.rateLimiter.canAdd()) plugin.logger.log(
                Level.INFO,
                String.format("Chunk without players found in %s at %d %d", world.name, x, z)
            )
        }
    }
}