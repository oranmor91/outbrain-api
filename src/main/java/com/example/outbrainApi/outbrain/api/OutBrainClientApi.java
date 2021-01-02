package com.example.outbrainApi.outbrain.api;

import com.example.outbrainApi.outbrain.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URISyntaxException;

public interface OutBrainClientApi {

    CampaignResponse getCampaigns(int offset, int limit);

    CampaignRetrive getCampaign(String campaignId);

    BudgetRetreive createBudget(BudgetCreate budget) throws Exception;

    PromotedLinks getPromotedLinks(String campaignId) throws Exception;

    CampaignRetrive createCampaign(CampaignCreate campaign) throws URISyntaxException, JsonProcessingException;

    PromotedLink createPromotedLink(String campaignId, PromotedLink promotedLink);
}
