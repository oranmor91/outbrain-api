package com.example.outbrainApi.outbrain.api;

import com.example.outbrainApi.outbrain.dto.CampaignRetrive;

import java.util.List;
import java.util.Set;

public class CreationCampaignsManger extends CampaignsManager<NewCampaigns> {

    private final List<String> newCampaignsForDates;
    private Set<String> allCampaignsNames;
    private final List<CampaignRetrive> originalCampaigns;
    private NewCampaigns newCampaigns;

    public CreationCampaignsManger(int numOfThreads, String token, String account, List<String> newCampaignsForDates,
                                   Set<String> allCampaignsNames, List<CampaignRetrive> originalCampaigns) {
        super(numOfThreads, token, account);
        this.newCampaignsForDates = newCampaignsForDates;
        this.allCampaignsNames = allCampaignsNames;
        this.originalCampaigns = originalCampaigns;
        this.newCampaigns = new NewCampaigns();
    }

    @Override
    protected NewCampaigns getResult() {
        return newCampaigns;
    }

    @Override
    protected int getCount() {
        return this.originalCampaigns.size();
    }

    @Override
    public CampaignsRunner<NewCampaigns> getCampaignsRunner(int firstIndex, int lastIndex) {
        return new CampaignCreator(this, firstIndex, lastIndex);
    }

    @Override
    public synchronized void addResult(NewCampaigns object) {
        newCampaigns.getCampaigns().addAll(object.getCampaigns());
    }

    public List<CampaignRetrive> getOriginalCampaigns() {
        return originalCampaigns;
    }

    public List<String> getNewCampaignsForDates() {
        return newCampaignsForDates;
    }

    public synchronized Set<String> getCampaignNames(){
        return this.allCampaignsNames;
    }

    public synchronized void addCampaignName(String newName){
        this.allCampaignsNames.add(newName);
    }
}
