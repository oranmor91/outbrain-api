package com.example.outbrainApi.outbrain.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CampaignsProcessor<T> {

    protected CampaignsManager<T> manager;
    private List<Future> futures;

    public CampaignsProcessor(CampaignsManager<T> manager) {
        this.manager = manager;
        this.futures = new ArrayList<>();
    }

    public T process(){

        ExecutorService executorService = Executors.newCachedThreadPool();
        int i = 0; int firstIndex = 0; int lastIndex;
        while (i <= manager.getNumOfThreads()){
            lastIndex = Math.min(firstIndex + manager.getCountsPerThread(), manager.getCount());
            futures.add(executorService.submit(manager.getCampaignsRunner(firstIndex, lastIndex)));
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

        return manager.getResult();
    }
}
