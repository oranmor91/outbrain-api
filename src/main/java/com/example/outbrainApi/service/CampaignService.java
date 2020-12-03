package com.example.outbrainApi.service;

import com.example.outbrainApi.model.CampaignsDuplication;
import com.example.outbrainApi.outbrain.api.OutBrainApi;
import com.example.outbrainApi.outbrain.api.OutBrainApiImpl;
import com.example.outbrainApi.outbrain.dto.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignService {

    private OutBrainApi outBrainApi;
    private Logger logger = LoggerFactory.getLogger(CampaignService.class);

    public CampaignService() {
        this.outBrainApi = new OutBrainApiImpl();
    }

    public void duplicateCampaigns(CampaignsDuplication campaignsDuplication){
        List<String> newCampaigns =
                outBrainApi.duplicateCampaigns(campaignsDuplication.getAccount(),
                campaignsDuplication.getNewCampaignForDates(), campaignsDuplication.getLastDuplicationDates());
        logger.info("Created {} campaigns for account: {}", newCampaigns.size(), campaignsDuplication.getAccount());
        newCampaigns.forEach(System.out::println);
    }
}
