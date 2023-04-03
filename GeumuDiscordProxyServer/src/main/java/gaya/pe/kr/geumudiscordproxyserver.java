package gaya.pe.kr;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.config.ProxyConfig;
import gaya.pe.kr.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.minecraft.option.manager.OptionManager;
import lombok.Getter;
import org.slf4j.Logger;

import java.net.DatagramSocket;

@Plugin(
        id = "geumudiscordproxyserver",
        name = "geumudiscordproxyserver",
        version = "1.0-SNAPSHOT"
)
@Getter
public class geumudiscordproxyserver {

    private final ProxyServer server;
    private final Logger logger;

    static Plugin plugin;

    DiscordManager discordManager = DiscordManager.getInstance();
    OptionManager optionManager = OptionManager.getInstance();


    @Inject
    public geumudiscordproxyserver(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        logger.info("GAYA_SOFT Plugin Start");
        plugin = (Plugin) server.getPluginManager()
                .getPlugin("geumudiscordproxyserver").orElse(null)
                .getInstance().get();

        discordManager.init();
        optionManager.init();

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
//        server.getEventManager().register(this, new PluginListener());

    }


    public static Plugin getPlugin() {
        return plugin;
    }
}
