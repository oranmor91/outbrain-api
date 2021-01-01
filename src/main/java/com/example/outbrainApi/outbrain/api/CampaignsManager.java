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

    public abstract CampaignsRunner<T> getCampaignsRunner(int firstIndex, int lastIndex);

    public int getCountsPerThread(){

        int totalCount = getCount();

        if (totalCount <= 0){
            return 0;
        }

        return (totalCount / numOfThreads) + 1;
    }

    public int getNumOfThreads() {
        return numOfThreads;
    }

    public void setNumOfThreads(int numOfThreads) {
        this.numOfThreads = numOfThreads;
    }

    public abstract void addResult(T object);

}
