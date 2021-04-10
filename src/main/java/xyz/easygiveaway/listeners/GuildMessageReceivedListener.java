package xyz.easygiveaway.listeners;

import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import xyz.easygiveaway.main.Main;
import xyz.easygiveaway.utils.Config;

import java.util.Arrays;
import java.util.List;

public class GuildMessageReceivedListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent ev) {

        Message msg = ev.getMessage();
        Member m = ev.getMember();
        TextChannel channel = ev.getChannel();
        String content = msg.getContentRaw();
        if (content.startsWith(Config.get("prefix"))) {
            List<String> args = Arrays.asList(content.substring(2).split(" "));
            if (!Main.instance.getCommandManager().execute(args.get(0), m, channel, msg, args)) {
                channel.sendMessage("That's not a valid command.").queue();
            }
        }
    }
}
