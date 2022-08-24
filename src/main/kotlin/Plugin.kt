import features.*
import fixes.*
import interfaces.CustomCommand
import interfaces.RepeatingTask
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level


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
            WatchChunkLoading(this),
            // Fixes
            CancelRemount(this),
            CrystalFix(this),
            FixContainerDupes(this),
            FixZombieDupe(this),
            NetherRoofTeleport(this),
            ResetSpeedAttributes(this),
            NameTagFilter(this),
            // Commands
            Commands(this)
        )

        logger.log(Level.INFO,
            ChatColor.translateAlternateColorCodes('&', "&aplugin enabled successfully")
        )

//        if (!this.config.getBoolean("disableMetrics"))
//            Metrics(this, -1)
    }

    private fun register(vararg modules: Any) {
        val disabledClasses = config.getStringList("disabledClasses")
        for (module in modules) {
            if (disabledClasses.contains(module.javaClass.simpleName))
                continue
            when(module) {
                is Listener -> server.pluginManager.registerEvents(module, this)
                is RepeatingTask -> Bukkit.getScheduler().scheduleSyncRepeatingTask(this, module, module.delay, module.period)
                is CustomCommand -> getCommand(module.command).executor = module
            }
        }
    }
}