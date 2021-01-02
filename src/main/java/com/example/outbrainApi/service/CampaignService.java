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

    public void duplicateCampaigns(CampaignsDuplication campaignsDuplication) {
        AccountDuplicationResult accountDuplicationResult = outBrainApi.duplicateCampaigns
                (campaignsDuplication.getAccount(), campaignsDuplication.getNewCampaignForDates(), campaignsDuplication.getLastDuplicationDates());
        logger.info("Created {} new campaigns for account: {}, The campaigns names: {}",
                accountDuplicationResult.getNewCampaigns().size(), campaignsDuplication.getAccount(),
                String.join("\n", accountDuplicationResult.getNewCampaigns()));

        if (!accountDuplicationResult.getFailureDuplications().isEmpty())
            logger.error("Failed to duplicate {} campaigns for account: {}, The original campaigns names: {}",
                    accountDuplicationResult.getFailureDuplications().size(), campaignsDuplication.getAccount(),
                    String.join("\n", accountDuplicationResult.getFailureDuplications()));
    }
}
