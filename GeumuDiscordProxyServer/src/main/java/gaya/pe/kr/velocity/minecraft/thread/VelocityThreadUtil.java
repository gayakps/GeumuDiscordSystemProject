package gaya.pe.kr.velocity.minecraft.thread;


import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import gaya.pe.kr.velocity.minecraft.geumudiscordproxyserver;

import java.util.concurrent.TimeUnit;

public class VelocityThreadUtil {

    static ProxyServer server;

    public static void init(ProxyServer paramProxyServer) {
        server = paramProxyServer;
    }


    public static ScheduledTask delayTask(Runnable runnable, int delayMillSec) {
       return server.getScheduler()
                .buildTask(server.getPluginManager().getPlugin("geumudiscordproxyserver").get().getInstance().get(), runnable)
                .delay(delayMillSec, TimeUnit.MILLISECONDS)
                .schedule();
    }

    public static ScheduledTask repeatTask(Runnable runnable, int delayMillSec) {
        return server.getScheduler()
                .buildTask(server.getPluginManager().getPlugin("geumudiscordproxyserver").get().getInstance().get(), runnable)
                .repeat(delayMillSec, TimeUnit.MILLISECONDS)
                .schedule();
    }

    public static Thread asyncTask(Runnable runnable) {

        Thread thread = new Thread(runnable);
        thread.start();

        return thread;

    }



}
