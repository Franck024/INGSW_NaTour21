package com.example.natour21.multithreading;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadManager {
    private static ThreadManager instance;
    private ExecutorService executorService = null;

    private ThreadManager(){
        executorService = Executors.newCachedThreadPool();
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public <T> Future<T> submit(Callable<T> callable){
        return executorService.submit(callable);
    }

    public Future<?> submit(Runnable runnable){
        return executorService.submit(runnable);
    }

    public static ThreadManager getInstance(){
        if (instance == null || instance.getExecutorService().isShutdown()){
            instance = new ThreadManager();
        }
        return instance;
    }
}
