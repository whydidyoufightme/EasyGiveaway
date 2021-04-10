package xyz.easygiveaway.utils;

import net.dv8tion.jda.api.entities.TextChannel;
import xyz.easygiveaway.mysql.MySQL;
import xyz.easygiveaway.mysql.SQLManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Giveaway {

    public static String getGiveawayEmoji(long messageIdLong, TextChannel channel) {
        ResultSet set = MySQL.getResult("SELECT * FROM " + SQLManager.giveawaytable + " WHERE guildId= '" + channel.getGuild().getIdLong() + "' AND messageId= '" + messageIdLong + "' AND channelId= '" + channel.getIdLong() + "';");
        if (set != null) {
            try {
                if (set.next()) {
                    return set.getString("emoji");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
