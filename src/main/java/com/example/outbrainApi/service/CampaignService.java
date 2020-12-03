package com.example.outbrainApi.service;

import com.example.outbrainApi.model.CampaignsDuplication;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {

    public CampaignService() {
    }

    public void printCampaign(CampaignsDuplication campaignsDuplication) {
        System.out.println(campaignsDuplication.getLastDuplicationDates());
    }
}
