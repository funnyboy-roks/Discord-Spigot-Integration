package com.funnyboyroks.DiscordIntegration.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import com.funnyboyroks.markdown.MarkdownProcessor;

import javax.security.auth.login.LoginException;

public class BotUtils {

    private static Bot discordBot;

    public static void discordChatMessage(String msg){
        discordBot.getDiscordInGame().sendMessage(sanitise(msg)).queue();
//        System.out.println();
    }

    public static void discordChatMessage(AsyncPlayerChatEvent event){
        discordBot.getDiscordInGame().sendMessage(String.format("<%s> %s", event.getPlayer().getDisplayName(), sanitise(event.getMessage()))).queue();
    }

    public static String sanitise(String str){
        String out = str;
        out = ChatColor.stripColor(out);
        out = out.replaceAll("@", "@ ");
        out = out.replaceAll("\\*", "\\*");
        out = out.replaceAll("_", "\\_");
        out = out.replaceAll("~", "\\~");
        out = out.replaceAll("\\|", "\\|");
        return out;

    }

    public static Bot createBot() throws LoginException, InterruptedException {
        discordBot = new Bot();
        return discordBot;
    }

    public static Bot getBot() {
        return discordBot;
    }

    public static void sendDiscordMessage(MessageReceivedEvent event) {
        MarkdownProcessor mp = new MarkdownProcessor();
        String msg = mp.markdown(event.getMessage().getContentRaw());
        Bukkit.getOnlinePlayers().forEach(x ->{
            x.sendMessage(ChatColor.DARK_GRAY + "DISCORD " + ChatColor.RESET + "" + event.getAuthor().getName() + ": " + msg);
        });
    }
}
