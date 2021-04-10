package xyz.easygiveaway.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import xyz.easygiveaway.main.Main;
import xyz.easygiveaway.mysql.MySQL;
import xyz.easygiveaway.mysql.SQLManager;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utilities {

    public static Role getModRole(Guild guild) {
        Role role = null;
        ResultSet set = MySQL.getResult("SELECT * FROM " + SQLManager.maintable + " WHERE guildId= '" + guild.getIdLong() + "';");
        if (set != null) {
            try {
                if (set.next()) {
                    long roleId = set.getLong("roleId");
                    role = guild.getRoleById(roleId);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return role;
    }
    public static Color getGuildColor(Guild guild) {
        Color color = Color.decode(Config.get("color"));
        ResultSet set = MySQL.getResult("SELECT * FROM " + SQLManager.maintable + " WHERE guildId= '" + guild.getIdLong() + "';");
        if (set != null) {
            try {
                if (set.next()) {
                    color = Color.decode(String.valueOf(set.getInt("color")));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return color;
    }
    public static String getGuildEmoji(Guild guild) {
        String emoji = "🎉";
        ResultSet set = MySQL.getResult("SELECT * FROM " + SQLManager.maintable + " WHERE guildId= '" + guild.getIdLong() + "';");
        if (set != null) {
            try {
                if (set.next()) {
                    emoji = set.getString("emoji");
                    return emoji;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return emoji;
    }
    public static void missingSetup(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(0x5634cf)
                .setDescription("Sorry, this guild has no setup.\nType `g.setup` to create one.");
        channel.sendMessage(builder.build()).queue(null,
                new ErrorHandler()
                        .ignore(ErrorResponse.MISSING_PERMISSIONS)
                        .ignore(ErrorResponse.MISSING_ACCESS)
                        .ignore(ErrorResponse.UNKNOWN_CHANNEL)
                        .ignore(ErrorResponse.EMBED_DISABLED)
        );
    }
    public static ErrorHandler getDefaultErrorHandler() {
        return new ErrorHandler().ignore(ErrorResponse.MISSING_ACCESS).ignore(ErrorResponse.MISSING_PERMISSIONS);
    }
    public static User getOwner() {
        return Main.getJda().getUserById(Config.get("owner_id"));
    }
}
