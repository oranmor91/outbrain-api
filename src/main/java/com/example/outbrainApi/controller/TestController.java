package com.example.outbrainApi.controller;

import com.example.outbrainApi.model.CampaignsDuplication;
import com.example.outbrainApi.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.outbrainApi.outbrain.api.OutBrainApiImpl;
import com.example.outbrainApi.outbrain.dto.Token;

import java.util.List;

@RequestMapping("/api/v1/campaign")
@RestController
public class TestController {

    private final CampaignService campaignService;

    @Autowired
    public TestController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping
    public void duplicateCampaigns(@RequestBody CampaignsDuplication campaignsDuplication){
        OutBrainApiImpl outBrainApi = new OutBrainApiImpl(Token.TARZO_TOKEN, campaignsDuplication.getAccount(),
                campaignsDuplication.getNewCampaignForDates(), campaignsDuplication.getLastDuplicationDates());
        List<String> newCampaigns = outBrainApi.duplicateCampaigns();
        System.out.println("created " + newCampaigns.size() + " campaigns for account: " + campaignsDuplication.getAccount());
        newCampaigns.forEach(System.out::println);
        campaignService.printCampaign(campaignsDuplication);
    }

}
