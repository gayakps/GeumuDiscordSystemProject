package gaya.pe.kr.core.thread.abstaract;

import gaya.pe.kr.core.thread.SchedulerUtil;

public abstract class RepeatSchedulerThread implements Runnable {
    int taskId;
    boolean running;

    public void start(int runningTick) {
        setRunning(true);
        taskId = SchedulerUtil.scheduleRepeatingTask(this, 0, runningTick);
    }

    public void interrupt() {
        setRunning(false);
        SchedulerUtil.cancel(taskId);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

}
