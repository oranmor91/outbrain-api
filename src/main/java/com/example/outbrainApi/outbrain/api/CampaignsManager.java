package com.example.outbrainApi.outbrain.api;


public abstract class CampaignsManager<T> {
    protected int numOfThreads;
    protected String token;
    protected String account;

    public CampaignsManager(int numOfThreads, String token, String account) {
        this.numOfThreads = numOfThreads;
        this.token = token;
        this.account = account;
    }

    protected abstract T getResult();

    protected abstract int getCount();

    abstract CampaignsRunner<T> getCampaignsRunner(int firstIndex, int lastIndex);

    protected int getCountsPerThread(){

        int totalCount = getCount();

        if (totalCount <= 0){
            return 0;
        }

        if (totalCount == numOfThreads){
            return 1;
        }

        return (totalCount / numOfThreads) + 1;
    }

    public int getNumOfThreads() {
        return numOfThreads;
    }

    public abstract void addResult(T object);

}
