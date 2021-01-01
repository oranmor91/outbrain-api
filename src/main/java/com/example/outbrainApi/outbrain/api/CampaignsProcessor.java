package com.example.outbrainApi.outbrain.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CampaignsProcessor<T> {

//    private boolean finished;
    protected CampaignsManager<T> manager;
//    private long timeout;
    private List<Future> futures;

    public CampaignsProcessor(CampaignsManager<T> manager, long timeout) {
        this.manager = manager;
//        this.timeout = timeout;
        this.futures = new ArrayList<>();
    }

    public T process(){

//// now add to it:
//        futures.add(executorInstance.submit(new Callable<Void>() {
//            public Void call() throws IOException {
//                // do something
//                return null;
//            }
//        }));

        ExecutorService executorService = Executors.newCachedThreadPool();
        int i = 0; int firstIndex = 0; int lastIndex;
        while (i <= manager.getNumOfThreads()){
            lastIndex = Math.min(firstIndex + manager.getCountsPerThread(), manager.getCount());
            futures.add(executorService.submit(manager.getCampaignsRunner(firstIndex, lastIndex)));
//            executorService.execute(manager.getCampaignsRunner(firstIndex, lastIndex));
            firstIndex = lastIndex + 1;
            if (firstIndex >= manager.getCount()){
                firstIndex = manager.getCount() -1;
            }
            i++;
        }


        for(Future f: this.futures) {
            try {
                f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
//        try {
//            finished = executorService.awaitTermination(timeout, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            System.out.println(finished);
//            e.printStackTrace();
//        }

        return manager.getResult();
    }
}
