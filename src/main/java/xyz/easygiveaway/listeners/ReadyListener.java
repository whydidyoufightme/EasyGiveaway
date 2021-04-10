package xyz.easygiveaway.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.easygiveaway.main.Main;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReadyListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadyListener.class);
    @Override
    public void onReady(@NotNull ReadyEvent ev) {
        LOGGER.info("Logged in as " + Main.getJda().getSelfUser().getAsTag());
    }
}
