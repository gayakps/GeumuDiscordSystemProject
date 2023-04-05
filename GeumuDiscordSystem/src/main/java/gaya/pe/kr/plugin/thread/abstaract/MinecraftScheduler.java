package gaya.pe.kr.plugin.thread.abstaract;

import gaya.pe.kr.plugin.thread.SchedulerUtil;
import gaya.pe.kr.plugin.util.EventUtil;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class MinecraftScheduler implements Listener, Runnable {

    int taskId;
    boolean running;

    public void start(int runningTick) {
        setRunning(true);
        taskId = SchedulerUtil.scheduleRepeatingTask(this, 0, runningTick);
        register();
    }

    protected void interrupt() {
        setRunning(false);
        SchedulerUtil.cancel(taskId);
        unRegister();
    }

    protected void register() {
        EventUtil.register(this);
    }

    protected void unRegister() {
        HandlerList.unregisterAll(this);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
