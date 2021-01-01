package com.example.outbrainApi.outbrain.api;

public abstract class CampaignsRunner<T> implements Runnable {

    protected CampaignsManager<T> campaignsManager;
    protected int firstIndex;
    protected int lastIndex;

    public CampaignsRunner(CampaignsManager<T> campaignsManager, int firstIndex, int lastIndex){
        this.campaignsManager = campaignsManager;
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
    }

    public abstract void run();
}
