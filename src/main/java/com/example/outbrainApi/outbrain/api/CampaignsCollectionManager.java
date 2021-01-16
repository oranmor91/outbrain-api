package com.example.outbrainApi.outbrain.api;

import java.util.List;

public class CampaignsCollectionManager extends CampaignsManager<CampaignsCollection> {

    private OutBrainClientApi outBrainClientApi;
    private List<String> lastDuplicationDates;
    private CampaignsCollection campaignsCollection;
    private int totalCount;

    public CampaignsCollectionManager(int numOfThreads, String token, String account, List<String> lastDuplicationDates) {
        super(numOfThreads, token, account);
        outBrainClientApi = new OutBrainClientImpl(token, account);
        this.totalCount = outBrainClientApi.getCampaigns(0, 50).getTotalCount();
        this.campaignsCollection = new CampaignsCollection();
        this.lastDuplicationDates = lastDuplicationDates;
    }

    @Override
    protected CampaignsCollection getResult() {
        return campaignsCollection;
    }

    @Override
    protected int getCount() {
        return totalCount;
    }

    @Override
    public CampaignsRunner<CampaignsCollection> getCampaignsRunner(int firstIndex, int lastIndex) {
        return new CampaignCollector(this, firstIndex, lastIndex);
    }

    @Override
    public synchronized void addResult(CampaignsCollection campaignsCollection) {
        this.campaignsCollection.getOriginalCampaigns().addAll(campaignsCollection.getOriginalCampaigns());
        this.campaignsCollection.getAllCampaignsNames().addAll(campaignsCollection.getAllCampaignsNames());
    }

    public List<String> getLastDuplicationDates() {
        return lastDuplicationDates;
    }

    public void setLastDuplicationDates(List<String> lastDuplicationDates) {
        this.lastDuplicationDates = lastDuplicationDates;
    }

}
