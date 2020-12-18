package outbrainApi.outbrain.api;

import com.example.outbrainApi.outbrain.dto.*;

public interface OutBrainClientApi {

    CampaignResponse getCampaigns(int offset);

    CampaignRetrive getCampaign(String campaignId);

    BudgetRetreive createBudget(BudgetCreate budget);

    PromotedLinks getPromotedLinks(String campaignId);

    CampaignRetrive createCampaign(CampaignCreate campaign);

    PromotedLink createPromotedLink(String campaignId, PromotedLink promotedLink);
}
