package com.example.outbrainApi.outbrain.api;

import java.util.ArrayList;
import java.util.List;

public class NewCampaigns {
    private List<String> campaigns;

    public NewCampaigns(List<String> campaigns) {
        this.campaigns = campaigns;
    }

    public NewCampaigns() {
        this.campaigns = new ArrayList<>();
    }

    public List<String> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<String> campaigns) {
        this.campaigns = campaigns;
    }
}
