package it.leo.main.pools;

import java.util.List;

import it.leo.main.data.tasks.DBTask;

public class ThreadPool {
    private final List<Thread> threads;
    private final int MAX_THREADS = 10;

    public ThreadPool() {
        threads = List.of(new Thread());
    }

    public void assignThread(DBTask<String, String> task) {
        var opt_thread = threads.stream().filter(thr -> thr.isAlive()).findFirst();
        Thread thread;
        if (opt_thread.isEmpty() && threads.size() < MAX_THREADS) {
            thread = new Thread();
        }
    } 
}
