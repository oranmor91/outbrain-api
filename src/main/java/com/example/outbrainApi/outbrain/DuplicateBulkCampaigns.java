//package com.example.outbrainApi.outbrain;
//
//import com.example.outbrainApi.outbrain.api.OutBrainApiImpl;
//import com.example.outbrainApi.outbrain.dto.Marketer;
//import com.example.outbrainApi.outbrain.dto.Token;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//public class DuplicateBulkCampaigns {
//
//    public static void main(String[] args) {
//        //TODO: (1) Choose account
//        String account = Marketer.OTHER_ACCOUNT;
//
//        //TODO (2) Choose the last duplication date
//        List<String> lastDuplicationDates = Arrays.asList("2020-12-02");
//
//        //TODO (3) Choose the start dates for the new campaigns
//        List<String> newCampaignForDates = Collections.singletonList("2020-12-03");
//
//        OutBrainApiImpl outBrainApi = new OutBrainApiImpl(Token.TARZO_TOKEN, account, newCampaignForDates, lastDuplicationDates);
//        List<String> newCampaigns = outBrainApi.duplicateCampaigns(campaignsDuplication.getAccount(), campaignsDuplication.getNewCampaignForDates(), campaignsDuplication.getLastDuplicationDates());
//        System.out.println("created " + newCampaigns.size() + " campaigns for account: " + account);
//        newCampaigns.forEach(System.out::println);
//    }
//}
