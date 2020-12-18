package outbrainApi.outbrain.api;

import java.util.List;

public interface OutBrainApi {
    List<String> duplicateCampaigns(String account, List<String> newCampaignForDates, List<String> lastDuplicationDates);
}
