package xyz.easygiveaway.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.easygiveaway.utils.GuildCommand;

import java.util.List;

public class TestCommand implements GuildCommand {

    @Override
    public void executeCommand(Member m, TextChannel channel, Message msg, List<String> args) {

    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public String getHelp() {
        return null;
    }
}
