package com.example.outbrainApi.outbrain.api;

import com.example.outbrainApi.service.AccountDuplicationResult;

import java.util.List;

public interface OutBrainApi {
    AccountDuplicationResult duplicateCampaigns(String account, List<String> newCampaignForDates, List<String> lastDuplicationDates);
}
