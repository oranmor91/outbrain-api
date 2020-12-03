package com.example.outbrainApi.outbrain.api;

import com.example.outbrainApi.outbrain.dto.CampaignRetrive;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CampaignsCollection {

    private Set<String> allCampaignsNames;
    private List<CampaignRetrive> originalCampaigns;

    public CampaignsCollection(Set<String> allCampaignsNames, List<CampaignRetrive> originalCampaigns) {
        this.allCampaignsNames = allCampaignsNames;
        this.originalCampaigns = originalCampaigns;
    }

    public CampaignsCollection() {
        this.allCampaignsNames = new HashSet<>();
        this.originalCampaigns = new ArrayList<>();
    }

    public Set<String> getAllCampaignsNames() {
        return allCampaignsNames;
    }


    public List<CampaignRetrive> getOriginalCampaigns() {
        return originalCampaigns;
    }

}
