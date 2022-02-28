package features

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.PlayerInfoData
import org.bukkit.plugin.Plugin


class PacketFilter(var plugin: Plugin) {
    init {
        val manager: ProtocolManager = ProtocolLibrary.getProtocolManager()

        val hidePlayerUUIDs: List<String> = plugin.config.getStringList("packetFilter.hiddenUUIDs")

        manager.addPacketListener(object :
            PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
            override fun onPacketSending(event: PacketEvent) {
                val infoDatas: List<PlayerInfoData> = event.packet.playerInfoDataLists.read(0)
                val data: PlayerInfoData = infoDatas[0]
                if (!hidePlayerUUIDs.contains(event.player.uniqueId.toString())
                    && hidePlayerUUIDs.contains(data.profile.uuid.toString())
                ) {
                    event.isCancelled = true
                }
            }
        })
    }
}