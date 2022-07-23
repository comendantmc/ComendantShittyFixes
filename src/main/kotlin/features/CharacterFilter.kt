package features

import helpers.MutableInt
import helpers.Translit
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.plugin.Plugin


class CharacterFilter(var plugin: Plugin) : Listener {
    private val tellCmds = plugin.config.getStringList("characterFilter.tellCmds")
    private val replyCmds = plugin.config.getStringList("characterFilter.replyCmds")
    private val numbersFromPlayers: HashMap<String, MutableInt> = HashMap<String, MutableInt>()

    private fun cancelNumbers(player: String, message: String): Boolean {
        if (!plugin.config.getBoolean("spam.filterNumbers") || !message.matches(Regex("(.*)\\d{4}(.*)"))) return false
        val count: MutableInt? = numbersFromPlayers[player]
        if (count == null) numbersFromPlayers[player] = MutableInt() else count.increment()
        return count != null && count.get() > plugin.config.getInt("spam.maxNumber")
    }

    // Filter characters when anvil renaming
    @EventHandler(priority = EventPriority.HIGH)
    fun onAnvilRename(e: InventoryClickEvent) {
        if (e.isCancelled) return

        val ent = e.whoClicked
        if (ent is Player && e.inventory is AnvilInventory) {
            val regex = plugin.config.getString("characterFilter.anvilRegex")

            val view = e.view
            val rawSlot = e.rawSlot
            if (rawSlot == view.convertSlot(rawSlot) && rawSlot == 2) {
                if (e.currentItem != null) {
                    val meta = e.currentItem.itemMeta
                    if (meta != null && meta.hasDisplayName()) {
                        meta.displayName = meta.displayName.replace(regex.toRegex(), "").trim()
                        e.currentItem.itemMeta = meta
                    }
//                    if (meta.displayName == null || meta.displayName.isEmpty())
//                        e.isCancelled = true
                }
            }
        }
    }

    // Filter chat messages
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onChatEvent(event: AsyncPlayerChatEvent) {
        val msg = event.message
        if (cancelNumbers(event.player.name, msg)) {
            event.isCancelled = true
            return
        }
        val regex = plugin.config.getString("characterFilter.chatRegex")
        var message = msg.replace(regex.toRegex(), "")
        if (plugin.config.getBoolean("characterFilter.replaceAllToCyrillic"))
            message = if (plugin.config.getBoolean("characterFilter.whitelistNicknames")) {
//                Translit.translitPerWordWithWhitelist(message, Bukkit.getOnlinePlayers().map { it.name })

                Translit.translit(message)
            } else {
                Translit.translit(message)
            }

        if (message.isNotEmpty()) event.message = message else event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onCommandPreprocess(e: PlayerCommandPreprocessEvent) {
        val msg = e.message
        val params = listOf(*msg.substring(1).split(" ").toTypedArray())
        val cmd = params[0]
        val isReply = replyCmds.contains(cmd)
        if (!tellCmds.contains(cmd)) return
        if (cancelNumbers(e.player.name, msg)) {
            e.isCancelled = true
            return
        }
        if (!isReply && params.size < 3) {
            e.isCancelled = true
            e.player.sendMessage(ChatColor.RED.toString() + "Использование: /w <игрок> <сообщение>")
            return
        } else if (isReply && params.size < 3) {
            e.isCancelled = true
            e.player.sendMessage(ChatColor.RED.toString() + "Использование: /r <сообщение>")
            return
        }
        val reciever = if (isReply) "" else " " + params[1]
        if (!isReply && reciever.equals(" " + plugin.config.getString("spam.adminUsername"), ignoreCase = true)) {
            e.player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("spam.adminPMReply")))
            e.isCancelled = true
            return
        }
        var input = java.lang.String.join(" ", params.subList(if (isReply) 1 else 2, params.size))
        val regex = plugin.config.getString("characterFilter.chatRegex")
        input = input.replace(regex.toRegex(), "")
        if (input.isEmpty()) {
            e.isCancelled = true
            return
        }
        if (plugin.config.getBoolean("characterFilter.replaceAllToCyrillic")) input = Translit.translit(input)
        e.message = "/$cmd$reciever $input"
    }
}

