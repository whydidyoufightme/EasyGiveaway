package xyz.easygiveaway.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import xyz.easygiveaway.utils.Giveaway;

public class GuildMessageReactionAddListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent ev) {
        long messageIdLong = ev.getMessageIdLong();
        TextChannel channel = ev.getChannel();
        MessageReaction reaction = ev.getReaction();
        Guild guild = ev.getGuild();
        User user = ev.getUser();
        if (Giveaway.getGiveawayEmoji(messageIdLong, channel) != null) {
            String emoji;
        }
    }
}
