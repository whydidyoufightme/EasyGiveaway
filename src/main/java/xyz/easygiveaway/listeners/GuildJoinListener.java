package xyz.easygiveaway.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.easygiveaway.main.Main;
import xyz.easygiveaway.utils.Utilities;

public class GuildJoinListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildJoinListener.class);

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent ev) {

        Guild guild = ev.getGuild();
        TextChannel systemChannel = guild.getSystemChannel();
        if (systemChannel == null) systemChannel = guild.getDefaultChannel();
        if (systemChannel == null) {
            LOGGER.warn("Could not send and introduction message for " + guild.getName());
            return;
        }
        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor(Main.getJda().getSelfUser().getAsTag(), null, Main.getJda().getSelfUser().getEffectiveAvatarUrl())
                .setColor(Utilities.getGuildColor(guild))
                .setDescription("**Hey,**\n\nThanks for inviting me. If you want to create a custom setup for faster giveaways simply type `g.setup`");
        try {
            systemChannel.sendMessage(builder.build()).queue(null,
                    Utilities.getDefaultErrorHandler()
            );
        } catch (MissingAccessException ex) {
            LOGGER.error("Could not send and introduction message for '" + guild.getName());
        }

    }
}
