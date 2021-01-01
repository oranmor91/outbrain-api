package com.example.outbrainApi.outbrain.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class OutBrainApiImpl implements OutBrainApi {
    private String pattern = "HH:mm:ss";
    private DateFormat df = new SimpleDateFormat(pattern);
    private String token;

    public OutBrainApiImpl() {
        token = System.getenv("token");
    }

    @Override
    public List<String> duplicateCampaigns(String account, List<String> newCampaignForDates, List<String> lastDuplicationDates) {

        CampaignsProcessor<CampaignsCollection> collectionCampaignsProcessor =
                new CampaignsProcessor<>(new CampaignsCollectionManager(10, token, account, lastDuplicationDates), 10);
        CampaignsCollection campaignsCollection = collectionCampaignsProcessor.process();


        CampaignsProcessor<NewCampaigns> creationCampaignsProcessor =
                new CampaignsProcessor<>(new CreationCampaignsManger(10, token, account, newCampaignForDates,
                        campaignsCollection.getAllCampaignsNames(), campaignsCollection.getOriginalCampaigns()), 600);

        return creationCampaignsProcessor.process().getCampaigns();
    }
}



