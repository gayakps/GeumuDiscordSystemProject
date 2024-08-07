package gaya.pe.kr.velocity.minecraft;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.network.manager.NetworkManager;
import gaya.pe.kr.velocity.minecraft.option.manager.ServerOptionManager;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import lombok.Getter;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

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

    ServerOptionManager optionManager = ServerOptionManager.getInstance();




    @Inject
    public geumudiscordproxyserver(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        logger.info("GAYA_SOFT Plugin Start");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
//        server.getEventManager().register(this, new PluginListener());
        VelocityThreadUtil.init(server);
        optionManager.init();
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return logger;
    }

}
