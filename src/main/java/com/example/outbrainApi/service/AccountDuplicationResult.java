package com.example.outbrainApi.service;

import java.util.ArrayList;
import java.util.List;

public class AccountDuplicationResult {

    private List<String> newCampaigns;
    private List<String> failureDuplications;

    public AccountDuplicationResult() {
        this.newCampaigns  = new ArrayList<>();
        this.failureDuplications = new ArrayList<>();
    }

    public AccountDuplicationResult(List<String> newCampaigns, List<String> failureDuplications) {
        this.newCampaigns = newCampaigns;
        this.failureDuplications = failureDuplications;
    }

    public List<String> getNewCampaigns() {
        return newCampaigns;
    }

    public void setNewCampaigns(List<String> newCampaigns) {
        this.newCampaigns = newCampaigns;
    }

    public List<String> getFailureDuplications() {
        return failureDuplications;
    }

    public void setFailureDuplications(List<String> failureDuplications) {
        this.failureDuplications = failureDuplications;
    }
}
