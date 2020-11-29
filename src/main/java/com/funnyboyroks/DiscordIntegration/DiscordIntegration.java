package com.funnyboyroks.DiscordIntegration;

import com.funnyboyroks.DiscordIntegration.discord.Bot;
import com.funnyboyroks.DiscordIntegration.discord.BotUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public final class DiscordIntegration extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);

        try {
            BotUtils.createBot();
        } catch (LoginException | InterruptedException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR CREATING BOT!");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
