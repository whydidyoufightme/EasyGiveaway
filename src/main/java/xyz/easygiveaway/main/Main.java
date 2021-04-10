package xyz.easygiveaway.main;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.easygiveaway.listeners.GuildJoinListener;
import xyz.easygiveaway.listeners.GuildMessageReceivedListener;
import xyz.easygiveaway.listeners.ReadyListener;
import xyz.easygiveaway.mysql.MySQL;
import xyz.easygiveaway.utils.CommandManager;
import xyz.easygiveaway.utils.Config;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static JDABuilder builder;
    public static JDA jda;
    public static Main instance;

    public CommandManager commandManager;
    public EventWaiter waiter = new EventWaiter();

    private final MySQL mySQL;
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Starting ...");
        new Main();
    }

    public Main() {

        instance = this;

        this.mySQL = new MySQL(this);
        mySQL.connect("localhost", 3306, "easygiveaway", "root", "");


        builder = JDABuilder.createDefault(Config.get("token"),

                // Direct Messages
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                // Guilds
                GatewayIntent.GUILD_EMOJIS,
                GatewayIntent.GUILD_INVITES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES

        );
        builder.disableCache(CacheFlag.VOICE_STATE);
        // builder.enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
        registerListener();
        registerCommands();
        try {
            jda = builder.build();
        } catch (LoginException ex) {
            ex.printStackTrace();
        }
        registerShutdown();
    }

    public void registerListener() {

        builder.addEventListeners(waiter, new ReadyListener(), new GuildJoinListener(), new GuildMessageReceivedListener());
    }
    public void registerCommands() {

        this.commandManager = new CommandManager(waiter);
    }

    public void registerShutdown() {
        new Thread(() -> {
            String input;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                while ((input = reader.readLine()) != null) {
                    if (input.equalsIgnoreCase("stop")) {
                        mySQL.disconnect();
                        if (jda != null) {
                            jda.getPresence().setStatus(OnlineStatus.OFFLINE);
                            jda.shutdown();
                            LOGGER.info(jda.getSelfUser().getAsTag() + " has been shut down.");
                        }
                        reader.close();
                        break;
                    } else {
                        LOGGER.warn("Use 'stop' to shut down.");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }


    // returns
    public static JDA getJda() {
        return jda;
    }
    public CommandManager getCommandManager() {
        return this.commandManager;
    }

}
