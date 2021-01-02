package com.example.outbrainApi.outbrain.api;

import com.example.outbrainApi.outbrain.dto.CampaignRetrive;
import com.example.outbrainApi.service.AccountDuplicationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreationCampaignsManger extends CampaignsManager<AccountDuplicationResult> {

    private final List<String> newCampaignsForDates;
    private Set<String> allCampaignsNames;
    private final List<CampaignRetrive> originalCampaigns;
    private AccountDuplicationResult accountDuplicationResult;

    public CreationCampaignsManger(int numOfThreads, String token, String account, List<String> newCampaignsForDates,
                                   Set<String> allCampaignsNames, List<CampaignRetrive> originalCampaigns) {
        super(numOfThreads, token, account);
        this.newCampaignsForDates = newCampaignsForDates;
        this.allCampaignsNames = allCampaignsNames;
        this.originalCampaigns = originalCampaigns;

    }

    @Override
    protected AccountDuplicationResult getResult() {
        return accountDuplicationResult;
    }

    @Override
    protected int getCount() {
        return this.originalCampaigns.size();
    }

    @Override
    public CampaignsRunner<AccountDuplicationResult> getCampaignsRunner(int firstIndex, int lastIndex) {
        return new CampaignCreator(this, firstIndex, lastIndex);
    }

    @Override
    public synchronized void addResult(AccountDuplicationResult accountDuplicationResult) {
        this.accountDuplicationResult.getNewCampaigns().addAll(accountDuplicationResult.getNewCampaigns());
        this.accountDuplicationResult.getFailureDuplications().addAll(accountDuplicationResult.getFailureDuplications());
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
