package com.funnyboyroks.DiscordIntegration;

import com.funnyboyroks.DiscordIntegration.discord.Bot;
import com.funnyboyroks.DiscordIntegration.discord.BotUtils;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        BotUtils.discordChatMessage(event);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        BotUtils.discordChatMessage(event.getDeathMessage());
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        BotUtils.discordChatMessage(event.getJoinMessage());
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        BotUtils.discordChatMessage(event.getQuitMessage());
    }

}
