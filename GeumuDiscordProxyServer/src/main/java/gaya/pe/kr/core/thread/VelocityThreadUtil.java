package gaya.pe.kr.core.thread;


import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import gaya.pe.kr.geumudiscordproxyserver;

import java.util.concurrent.TimeUnit;

public class VelocityThreadUtil {

    static ProxyServer server;
    static Plugin plugin;

    public static void init(ProxyServer paramProxyServer) {
        server = paramProxyServer;
        plugin = geumudiscordproxyserver.getPlugin();
    }


    public static ScheduledTask delayTask(Runnable runnable, int delayMillSec) {
       return server.getScheduler()
                .buildTask(plugin, runnable)
                .delay(delayMillSec, TimeUnit.MILLISECONDS)
                .schedule();
    }

    public static ScheduledTask repeatTask(Runnable runnable, int delayMillSec) {
        return server.getScheduler()
                .buildTask(plugin, runnable)
                .repeat(delayMillSec, TimeUnit.MILLISECONDS)
                .schedule();
    }

    public static Thread asyncTask(Runnable runnable) {

        Thread thread = new Thread(runnable);
        thread.start();

        return thread;

    }



}
