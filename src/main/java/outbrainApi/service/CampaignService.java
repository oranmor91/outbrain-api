package outbrainApi.service;

import com.example.outbrainApi.model.CampaignsDuplication;
import com.example.outbrainApi.outbrain.api.OutBrainApi;
import com.example.outbrainApi.outbrain.api.OutBrainApiImpl;
import com.example.outbrainApi.outbrain.dto.Token;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignService {

    private OutBrainApi outBrainApi;

    public CampaignService() {
        this.outBrainApi = new OutBrainApiImpl();
    }

    public void duplicateCampaigns(CampaignsDuplication campaignsDuplication){
        List<String> newCampaigns =
                outBrainApi.duplicateCampaigns(campaignsDuplication.getAccount(),
                campaignsDuplication.getNewCampaignForDates(), campaignsDuplication.getLastDuplicationDates());
        System.out.println("Created " + newCampaigns.size() + " campaigns for account: " + campaignsDuplication.getAccount());
        newCampaigns.forEach(System.out::println);
    }
}
