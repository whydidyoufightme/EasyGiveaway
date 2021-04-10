package xyz.easygiveaway.utils;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.easygiveaway.commands.GiveawayCommand;
import xyz.easygiveaway.commands.TestCommand;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {

    public ConcurrentHashMap<String, GuildCommand> commands;

    public CommandManager(EventWaiter waiter) {
        this.commands = new ConcurrentHashMap<>();
        this.commands.put("test", new TestCommand());

        // Starting giveaways
        this.commands.put("start", new GiveawayCommand(waiter));
        this.commands.put("create", new GiveawayCommand(waiter));
    }

    public boolean execute(String command, Member m, TextChannel channel, Message msg, List<String> args) {

        GuildCommand cmd;
        if ((cmd = this.commands.get(command)) != null) {
            cmd.executeCommand(m, channel, msg, args);
            return true;
        }
        return false;
    }
}
