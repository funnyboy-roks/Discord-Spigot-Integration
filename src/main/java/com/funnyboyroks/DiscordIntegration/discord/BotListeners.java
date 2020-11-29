package com.funnyboyroks.DiscordIntegration.discord;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

public class BotListeners extends ListenerAdapter {
    public void onReady(ReadyEvent e) {
        System.out.println("Bot is ready with the id of " + e.getJDA().getSelfUser().getId());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // SEND MESSAGE IN MINECRAFT
        if(!event.getAuthor().isBot()) {
            BotUtils.sendDiscordMessage(event);
        }
    }


}