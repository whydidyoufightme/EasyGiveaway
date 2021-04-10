package xyz.easygiveaway.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;
import xyz.easygiveaway.main.Main;
import xyz.easygiveaway.mysql.MySQL;
import xyz.easygiveaway.mysql.SQLManager;
import xyz.easygiveaway.utils.Config;
import xyz.easygiveaway.utils.GuildCommand;
import xyz.easygiveaway.utils.Utilities;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GiveawayCommand implements GuildCommand {

    private final EventWaiter waiter;

    public GiveawayCommand(EventWaiter waiter) {
        this.waiter = waiter;
    }


    @Override
    public void executeCommand(Member m, TextChannel channel, Message msg, List<String> args) {

        Role modRole = Utilities.getModRole(msg.getGuild());
        if (modRole != null) {
            if (m.getRoles().contains(modRole)) {
                if (args.size() != 3) {
                    channel.sendMessage(m.getAsMention() + ", please use `g.start #channel [number of winners]`.").queue(null,
                            Utilities.getDefaultErrorHandler()
                    );
                    return;
                }
                int winnerAmount;
                try {
                    winnerAmount = Integer.parseInt(args.get(2));
                } catch (NumberFormatException ex) {
                    channel.sendMessage(m.getAsMention() + ", please use `g.start #channel [number of winners]`.").queue(null,
                            Utilities.getDefaultErrorHandler()
                    );
                    return;
                }
                List<TextChannel> mentionedChannels = msg.getMentionedChannels();
                if (!mentionedChannels.isEmpty()) {
                    final TextChannel giveawayChannel = mentionedChannels.get(0);
                    EmbedBuilder builder = new EmbedBuilder()
                            .setColor(Color.decode(Config.get("color")))
                            .setDescription("Please enter a prize to win. (30s)");
                    channel.sendMessage(builder.build()).queue((waitingForPrizeMessage) -> {
                                this.waiter.waitForEvent(
                                        GuildMessageReceivedEvent.class,
                                        (ev) -> ev.getChannel().getIdLong() == channel.getIdLong() && ev.getAuthor().getIdLong() == m.getIdLong(),
                                        (ev) -> {
                                            final Message prizeMessage = ev.getMessage();
                                            final String prize = prizeMessage.getContentRaw();
                                            channel.sendMessage("Please enter the duration of the giveaway. You can choose every time between `1s` and ´14d` (30s)").queue((waitingForDurationMessage) -> {
                                                this.waiter.waitForEvent(
                                                        GuildMessageReceivedEvent.class,
                                                        (ev2) -> ev2.getChannel().getIdLong() == channel.getIdLong() && ev2.getAuthor().getIdLong() == m.getIdLong(),
                                                        (ev2) -> {
                                                            final Message durationMessage = ev2.getMessage();
                                                            final String rawTime = durationMessage.getContentRaw();
                                                            List<String> durationArgs = Arrays.asList(rawTime.split(" "));
                                                            if (durationArgs.size() <= 2) {
                                                                int rawDuration;
                                                                String unit;
                                                                int duration;
                                                                if (durationArgs.size() == 2) {
                                                                    try {
                                                                        duration = Integer.parseInt(durationArgs.get(0));
                                                                    } catch (NumberFormatException ex) {
                                                                        channel.sendMessage("Please enter a valid duration next time.").queue(null,
                                                                                Utilities.getDefaultErrorHandler()
                                                                        );
                                                                        return;
                                                                    }
                                                                    unit = durationArgs.get(1);
                                                                } else {
                                                                    unit = rawTime.substring(rawTime.length() - 1);
                                                                    duration = Integer.parseInt(rawTime.replace(unit, ""));
                                                                }
                                                                rawDuration = getTimeInSeconds(unit, duration);
                                                                createGiveaway(giveawayChannel, channel, prize, rawDuration, ev2.getAuthor(), winnerAmount);
                                                            } else {
                                                                channel.sendMessage("Please enter a valid duration like `9d`/`9 d`").queue(null,
                                                                        Utilities.getDefaultErrorHandler()
                                                                );

                                                            }
                                                        },
                                                        30L,
                                                        TimeUnit.SECONDS,
                                                        () -> channel.sendMessage(m.getAsMention() + ", your time ran out 😥").queue(null,
                                                                Utilities.getDefaultErrorHandler()
                                                        )
                                                );
                                            }, Utilities.getDefaultErrorHandler());
                                        },
                                        30L,
                                        TimeUnit.SECONDS,
                                        () -> channel.sendMessage(m.getAsMention() + ", your time ran out 😥").queue(null,
                                                Utilities.getDefaultErrorHandler()
                                        )
                                );
                            },
                            Utilities.getDefaultErrorHandler()
                    );

                } else {
                    channel.sendMessage("You didn't provide a channel for hosting the giveaway. Try it like `g.start #channel [number of winners]`.").queue(null,
                            Utilities.getDefaultErrorHandler()
                    );
                }
            } else {
                channel.sendMessage(new EmbedBuilder().setColor(Color.decode(Config.get("color"))).build()).queue(null,
                        new ErrorHandler()
                                .ignore(ErrorResponse.MISSING_PERMISSIONS)
                                .ignore(ErrorResponse.MISSING_ACCESS)
                );
            }

        } else {
            Utilities.missingSetup(channel);
        }

    }


    public void createGiveaway(TextChannel giveawayChannel, TextChannel channel, String prize, int time, User host, int winnerAmount) {

        /*
        Where? giveawayChannel,
        What? prize,
        How long? time,
        Who? host,
        How many winners? winnerAmount
         */

        if (time > 1209600) {
            channel.sendMessage("Giveaway can not hav a longer duration than 14 days.").queue(null, Utilities.getDefaultErrorHandler());
            return;
        }

        String emoji = Utilities.getGuildEmoji(channel.getGuild());
        OffsetDateTime endsAt = OffsetDateTime.now().plusSeconds(time);
        EmbedBuilder giveawayEmbed = new EmbedBuilder()
                .setAuthor("Giveaway", null, Main.getJda().getSelfUser().getEffectiveAvatarUrl())
                .setColor(Utilities.getGuildColor(channel.getGuild()))
                .setDescription("> **Prize:**" + prize + "\n> **" + winnerAmount + "** winners.")
                .setTimestamp(endsAt)
                .setFooter("hosted by " + host.getAsTag() + " | Ends at ", host.getEffectiveAvatarUrl());

        RestAction<Message> action = giveawayChannel.sendMessage(giveawayEmbed.build());
        long msgid = 10L;
        try {
            msgid = action.complete(false).getIdLong();
            action.complete(false).addReaction(emoji).queue();
        } catch (RateLimitedException ex) {
            ex.printStackTrace();
        }
        final long id = msgid;
        action.queue();


        MySQL.update("INSERT INTO " + SQLManager.giveawaytable + " (guildId, channelId, messageId, emoji) VALUES ('" + channel.getGuild().getIdLong() + "','" + giveawayChannel.getIdLong() + "','" + id + "','" + emoji + "');");

        // Success callback
        channel.sendMessage("Your giveaway started in " + giveawayChannel.getAsMention()).queue(null,
                Utilities.getDefaultErrorHandler()
        );

        // Timer
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, time);
        date = cal.getTime();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                Message giveawayMessage = giveawayChannel.retrieveMessageById(id).complete();
                if (giveawayMessage != null) {
                    ResultSet set = MySQL.getResult("SELECT * FROM " + SQLManager.giveawaytable + " WHERE guildId= '" + channel.getGuild().getIdLong() + "' AND channelId= '" + giveawayChannel.getIdLong() + "' AND messageId= '" + id + "';");
                    if (set != null) {
                        try {
                            if (set.next()) {
                                String giveawayEmoji = set.getString("emoji");
                                MessageReaction reaction = giveawayMessage.getReactions().get(0);
                                if ((reaction.getReactionEmote().isEmoji() && reaction.getReactionEmote().getEmoji().equals(giveawayEmoji)) || (reaction.getReactionEmote().isEmote() && reaction.getReactionEmote().getId().equals(giveawayEmoji))) {
                                    List<User> participants = reaction.retrieveUsers().complete();
                                    participants.remove(Main.getJda().getSelfUser());
                                    int amount_of_winners = set.getInt("winnerAmount");
                                    List<User> winners = new ArrayList<>();
                                    if (participants.size() >= amount_of_winners) {
                                        for (int i = 0; i < amount_of_winners; i++) {
                                            User winner = participants.get(new Random().nextInt(participants.size()));
                                            winners.add(winner);
                                            participants.remove(winner);
                                        }
                                    } else {
                                        giveawayMessage.editMessage(new EmbedBuilder().setColor(Color.red).setDescription("Giveaway ended. Too few participants.").build()).queue();
                                        giveawayChannel.sendMessage("I wasn't able to end the giveaway for '" + prize + "'. Missing **" + (amount_of_winners - participants.size()) + "** participants.").queue(null,
                                                Utilities.getDefaultErrorHandler()
                                        );
                                        return;
                                    }
                                    List<String> winnerMentions = new ArrayList<>();
                                    winners.forEach((w) -> winnerMentions.add(w.getAsMention()));
                                    EmbedBuilder winnerEmbed = new EmbedBuilder()
                                            .setAuthor("Giveaway ended", null, Main.getJda().getSelfUser().getEffectiveAvatarUrl())
                                            .setColor(Utilities.getGuildColor(channel.getGuild()))
                                            .setDescription("> " + String.join(", ", winnerMentions) + " won " + prize + ".\nThanks for participating ❣")
                                            .setFooter("hosted by " + host.getAsTag(), host.getEffectiveAvatarUrl());
                                    giveawayMessage.editMessage(winnerEmbed.build()).queue(null,
                                            Utilities.getDefaultErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (err) -> giveawayChannel.sendMessage("Sorry, I wasn't able to end the giveaway, because the giveaway message got deleted.").queue(null,
                                                    Utilities.getDefaultErrorHandler()
                                            ))
                                    );
                                    giveawayChannel.sendMessage("Congratulations " + String.join(", ", winnerMentions) + " you won '" + prize + "'.").queue(null,
                                            Utilities.getDefaultErrorHandler()
                                    );
                                }
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    MySQL.update("DELETE FROM " + SQLManager.giveawaytable + " WHERE WHERE guildId= '" + channel.getGuild().getIdLong() + "' AND channelId= '" + giveawayChannel.getIdLong() + "' AND messageId= '" + id + "';");
                }
            }
        };

        timer.schedule(task, date);
    }
    public static int getTimeInSeconds(String unit, int time) {

        return switch (unit.toLowerCase()) {
            case "m" -> time * 60;
            case "h" -> time * 3600;
            case "d" -> time * 86400;
            default -> time;
        };
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
