package com.example.outbrainApi.service;

import com.example.outbrainApi.model.CampaignsDuplication;
import com.example.outbrainApi.outbrain.api.OutBrainApi;
import com.example.outbrainApi.outbrain.api.OutBrainApiImpl;
import com.example.outbrainApi.outbrain.dto.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CampaignService {

    private OutBrainApi outBrainApi;
    private Logger logger = LoggerFactory.getLogger(CampaignService.class);

    public CampaignService() {
        this.outBrainApi = new OutBrainApiImpl();
    }

    public void duplicateCampaigns(CampaignsDuplication campaignsDuplication) {
        Map<String, String> accounts = createAccounts();

        AccountDuplicationResult accountDuplicationResult = outBrainApi.duplicateCampaigns
                (campaignsDuplication.getAccount(), campaignsDuplication.getNewCampaignForDates(), campaignsDuplication.getLastDuplicationDates());

        logger.info("Total campaigns number for account: {} is {}", accounts.get(campaignsDuplication.getAccount()),
                accountDuplicationResult.getNewCampaigns().size() + accountDuplicationResult.getFailureDuplications().size());

        logger.info("Created {} new campaigns for account: {}, The campaigns names: {}",
                accountDuplicationResult.getNewCampaigns().size(), accounts.get(campaignsDuplication.getAccount()),
                String.join("\n", accountDuplicationResult.getNewCampaigns()));

        if (!accountDuplicationResult.getFailureDuplications().isEmpty())
            logger.error("Failed to duplicate {} campaigns for account: {}, The original campaigns names: {}",
                    accountDuplicationResult.getFailureDuplications().size(), accounts.get(campaignsDuplication.getAccount()),
                    String.join("\n", accountDuplicationResult.getFailureDuplications()));
    }

    public Map<String, String> createAccounts(){
        Map<String, String> accounts = new HashMap<>();
        accounts.put("0047dc9c1732faa774d47d6e786216f6de", "US");
        accounts.put("00a24b4d819dd9fe8644c3518a182e982c", "DE");
        accounts.put("004d2962a27dd30e54706519caf6e2efff", "FR");
        accounts.put("00757806c1d46dba5ecf7200f25783c520", "NL");
        accounts.put("003e021f50e1a80bf7eb0170c6ae508ecd", "IT");
        accounts.put("003e35394f7ea4dbd4bc629bdd25936b39", "Hebrew");
        accounts.put("002e42140a89164a8c682fe3bd410a8139", "Other");

        return accounts;
    }

}
