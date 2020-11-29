package com.funnyboyroks.DiscordIntegration.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import javax.security.auth.login.LoginException;

public class Bot {

    private Guild discordGuild;
    private MessageChannel discordInGame;
    private MessageChannel discordVerify;
    private JDA build;

    public Bot() throws LoginException, InterruptedException {
        JDABuilder jdaBuilder = JDABuilder.createDefault("Nzc3OTg2MzA2NDkxNDE2NjA3.X7LaNQ.DI8xWytJywbnwM-NXSEQ0ysUmWc"); //("TOKEN", new ArrayList<GatewayIntent>());
        jdaBuilder.addEventListeners(new BotListeners());

        build = jdaBuilder.build();

        build.awaitReady();

        discordGuild = build.getGuildById("778029378562949140");
        discordInGame = build.getTextChannelById("778029484511199254");
        discordVerify = build.getTextChannelById("778029506518188073");

        if(discordGuild == null){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Can't get guild");
        }else{
            System.out.println(discordGuild);
        }
        if(discordInGame == null){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Can't get in-game channel");
        }else{
            System.out.println(discordInGame.getId());
        }
        if(discordVerify == null){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Can't get verify channel");
        }else{
            System.out.println(discordVerify);
        }

    }

    public JDA getBuild() {
        return build;
    }

    public Guild getDiscordGuild() {
        return discordGuild;
    }

    public MessageChannel getDiscordInGame() {
        return discordInGame;
    }

    public MessageChannel getDiscordVerify() {
        return discordVerify;
    }
}
