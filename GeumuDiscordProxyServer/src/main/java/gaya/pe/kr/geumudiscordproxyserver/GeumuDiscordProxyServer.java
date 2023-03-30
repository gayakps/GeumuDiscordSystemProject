package gaya.pe.kr.geumudiscordproxyserver;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "GeumuDiscordProxyServer",
        name = "GeumuDiscordProxyServer",
        version = "1.0-SNAPSHOT"
)
public class GeumuDiscordProxyServer {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
