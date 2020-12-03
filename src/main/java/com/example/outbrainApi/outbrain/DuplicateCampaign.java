package com.example.outbrainApi.outbrain;

import com.example.outbrainApi.outbrain.api.OutBrainApiImpl;
import com.example.outbrainApi.outbrain.dto.Campaign;
import com.example.outbrainApi.outbrain.dto.Marketer;
import com.example.outbrainApi.outbrain.dto.Token;

import java.util.Arrays;
import java.util.List;

public class DuplicateCampaign {

    public static void main(String[] args) {

        //TODO: (1) Choose account
        String account = Marketer.ENGLISH_ACCOUNT;

        //TODO (2) Choose the last duplication date
        String campaignId = "00b33e0c9a46357a59429ab380fd6b5a87";

        //TODO (3) Choose the start dates for the new campaigns
        List<String> newCampaignForDates = Arrays.asList("2020-12-01"/*, "2020-11-30"*/);


        OutBrainApiImpl outBrainApi = new OutBrainApiImpl(Token.TARZO_TOKEN, account, campaignId, newCampaignForDates);
        List<String> newCampaigns = outBrainApi.duplicateCampaigns();
        System.out.println("created " + newCampaigns.size() + " campaigns for account: " + account);
        newCampaigns.forEach(System.out::println);
    }
}
