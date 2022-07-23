package features

import helpers.Translit
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.plugin.Plugin

class NameTagFilter(val plugin : Plugin) : Listener {
    @EventHandler
    fun onNameTagUse(e: PlayerInteractEntityEvent) {
        if (e.player.inventory.itemInMainHand.type.equals(Material.NAME_TAG)) {
            val regex = plugin.config.getString("characterFilter.nameTagRegex")
            val blacklist = plugin.config.getStringList("characterFilter.nameTagBlacklist")

            val meta = e.player.inventory.itemInMainHand.itemMeta
            if (meta != null && meta.hasDisplayName()) {
                var name = meta.displayName
                if (blacklist.isNotEmpty()) {
                    name = name.replace(blacklist.joinToString("|").toRegex(), "").trim()
                }
                name = name.replace(regex.toRegex(), "").trim()

                if (plugin.config.getBoolean("characterFilter.replaceAllToCyrillicOnNameTags"))
                    name = if (plugin.config.getBoolean("characterFilter.whitelistNicknames")) {
                        Translit.translitPerWordWithWhitelist(name, Bukkit.getOfflinePlayers().map { it.name })
                    } else {
                        Translit.translit(name)
                    }

                if (name.isEmpty()) {
                    e.player.inventory.remove(e.player.inventory.itemInMainHand)
                    return
                }
                meta.displayName = name
                e.player.inventory.itemInMainHand.itemMeta = meta
            }
        }
    }
}