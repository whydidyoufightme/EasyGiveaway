package xyz.easygiveaway.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public interface GuildCommand {

    void executeCommand(Member m, TextChannel channel, Message msg, List<String> args);

    String getUsage();
    String getHelp();
}
