package live.myoun.hypnos

import io.papermc.paper.event.player.PlayerDeepSleepEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedLeaveEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.math.roundToInt

class Hypnos : JavaPlugin() {

    companion object {
        @JvmStatic
        val koreanMorningComponent = Component.text("아침이 밝았습니다!").color(NamedTextColor.GOLD)
        @JvmStatic
        val englishMorningComponent = Component.text("The morning has come!").color(NamedTextColor.GOLD)
    }


    val sleepingPlayersList = mutableSetOf<Player>()

    override fun onEnable() {
        server.pluginManager.registerEvents(listener(),this)
    }

    inner class listener : Listener {

        @EventHandler
        fun onSleep(event:PlayerDeepSleepEvent) {
            var onlinePlayers = 0
            for (pl in server.onlinePlayers)
                if (pl.world.environment == World.Environment.NORMAL) onlinePlayers ++
            sleepingPlayersList.add(event.player)
            val koreanComponent = Component.text("${event.player.name}님이 침대에 누웠습니다. ${sleepingPlayersList.size}/${onlinePlayers}").color(NamedTextColor.GOLD)
            val englishComponent = Component.text("${event.player.name} is sleeping. ${sleepingPlayersList.size}/${onlinePlayers}").color(NamedTextColor.GOLD)
            var canMorning = sleepingPlayersList.size >= onlinePlayers.div2()
            for (player in server.onlinePlayers) {
                when (player.locale()) {
                    Locale.KOREAN -> {
                        player.sendMessage(koreanComponent)
                        if (canMorning) player.sendMessage(koreanMorningComponent)
                    }
                    Locale.KOREA ->  {
                        player.sendMessage(koreanComponent)
                        if (canMorning) player.sendMessage(koreanMorningComponent)
                    }
                    else -> {
                        player.sendMessage(englishComponent)
                        if (canMorning) player.sendMessage(englishMorningComponent)
                    }
                }
            }
            if (canMorning) {
                for (world in server.worlds) {
                    if (world.environment == World.Environment.NORMAL) {
                        world.time = 1000
                        Bukkit.dispatchCommand(server.consoleSender,"weather thunder 1")
                    }
                }
                sleepingPlayersList.clear()
            }
        }

        @EventHandler
        fun onWakeUp(event:PlayerBedLeaveEvent) {
            sleepingPlayersList.remove(event.player)
            var onlinePlayers = 0
            for (pl in server.onlinePlayers)
                if (pl.world.environment == World.Environment.NORMAL) onlinePlayers ++
            val koreanComponent = Component.text("${event.player.name}님이 침대에서 일어났습니다. ${sleepingPlayersList.size}/${onlinePlayers}").color(NamedTextColor.GOLD)
            val englishComponent = Component.text("${event.player.name} is out of bed. ${sleepingPlayersList.size}/${onlinePlayers}").color(NamedTextColor.GOLD)
            if (event.player.world.time in 0..12999) return
            if (event.player !in sleepingPlayersList) return
            for (player in server.onlinePlayers) {
                when (player.locale()) {
                    Locale.KOREAN -> {
                        player.sendMessage(koreanComponent)
                    }
                    Locale.KOREA ->  {
                        player.sendMessage(koreanComponent)
                    }
                    else -> {
                        player.sendMessage(englishComponent)
                    }
                }
            }
        }
    }
}

private fun Int.div2():Int {
    return (this / 2).toDouble().roundToInt()
}