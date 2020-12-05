package com.example.outbrainApi.controller;

import com.example.outbrainApi.model.CampaignsDuplication;
import com.example.outbrainApi.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/campaign")
@RestController
public class DuplicateCampaignsController {

    private final CampaignService campaignService;

    @Autowired
    public DuplicateCampaignsController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping
    public void duplicateCampaigns(@RequestBody CampaignsDuplication campaignsDuplication){
        campaignService.duplicateCampaigns(campaignsDuplication);
    }
}
