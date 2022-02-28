import features.*
import fixes.*
import interfaces.CustomCommand
import interfaces.RepeatingTask
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin


@Suppress("UNUSED")
class Plugin : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig()

        register(
            // Features
            CharacterFilter(this),
            DupeStashFinder(this),
            StrictPositionCheck(this),
            LimitBlocksPerChunk(this),
            PacketFilter(this),
            StrictPositionCheck(this),
            WatchChunkLoading(this),
            // Fixes
            CancelRemount(this),
            CrystalFix(this),
            FixContainerDupes(this),
            FixZombieDupe(this),
            NetherRoofTeleport(this),
            ResetSpeedAttributes(this),
            // Commands
            Commands(this)
        )

//        if (!this.config.getBoolean("disableMetrics"))
//            Metrics(this, -1)
    }

    private fun register(vararg modules: Any) {
        for (module in modules) {
            if (module is Listener)
                server.pluginManager.registerEvents(module, this)
            if (module is RepeatingTask)
                Bukkit.getScheduler().scheduleSyncRepeatingTask(this, module, module.delay, module.period)
            if (module is CustomCommand)
                getCommand(module.command).executor = module
        }
    }
}