package com.example.outbrainApi.outbrain.api;

import com.example.outbrainApi.outbrain.dto.Campaign;
import com.example.outbrainApi.outbrain.dto.CampaignRetrive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CampaignCollector extends CampaignsRunner {

    private OutBrainClientApi outBrainClient;
    private Set<String> allCampaigns;
    private List<CampaignRetrive> relevantCampaigns;
    private Logger logger = LoggerFactory.getLogger(CampaignCollector.class);

    public CampaignCollector(CampaignsManager campaignsManager, int firstIndex, int lastIndex) {
        super(campaignsManager, firstIndex, lastIndex);
        this.outBrainClient = new OutBrainClientImpl(campaignsManager.token, campaignsManager.account);
        this.allCampaigns = new HashSet<>();
        this.relevantCampaigns = new ArrayList<>();
    }

    @Override
    public void run() {
            int offset = firstIndex;
            int lastOffset = lastIndex;
        CampaignsCollectionManager campaignsManager = (CampaignsCollectionManager) this.campaignsManager;
        logger.info("Start collecting {} campaigns from offset {}, to offset {}", campaignsManager.getCount(), firstIndex, lastIndex);
        while (offset <= lastOffset){
            if (lastOffset - offset <= 0){
                break;
            }
            int limit = Math.min(lastOffset - offset, 50);
            CampaignResponse response = outBrainClient.getCampaigns(offset, limit);
            offset += response.getCount();
            allCampaigns.addAll(response.getCampaigns().stream().map(Campaign::getName).collect(Collectors.toSet()));
            relevantCampaigns.addAll(response.getCampaigns().stream().filter(campaignRetrive -> {
                        return campaignsManager.getLastDuplicationDates().
                                contains(getDateByString(campaignRetrive.getBudget().getStartDate())) &&
                        !campaignRetrive.getLiveStatus().getOnAirReason().equals("CAMPAIGN_DISABLED") &&
                        !campaignRetrive.getLiveStatus().getOnAirReason().equals("PAUSED");
            })
                    .collect(Collectors.toList()));
        }

        CampaignsCollection campaignsCollection = new CampaignsCollection(allCampaigns, relevantCampaigns);

        campaignsManager.addResult(campaignsCollection);
    }

    private String getDateByString(Date campaignDate) {
        String pattern = "yyyy-MM-dd";

        DateFormat df = new SimpleDateFormat(pattern);

        return df.format(campaignDate);
    }
}
