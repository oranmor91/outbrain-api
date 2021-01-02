package com.example.outbrainApi.outbrain.api;

import com.example.outbrainApi.outbrain.dto.*;
import com.example.outbrainApi.service.AccountDuplicationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CampaignCreator extends CampaignsRunner {

    private AccountDuplicationResult duplicationResult;
    private OutBrainClientApi outBrainClientApi;
    private Logger logger = LoggerFactory.getLogger(CampaignCreator.class);

    public CampaignCreator(CampaignsManager campaignsManager, int firstIndex, int lastIndex) {
        super(campaignsManager, firstIndex, lastIndex);
        this.outBrainClientApi = new OutBrainClientImpl(campaignsManager.token, campaignsManager.account);
        this.duplicationResult = new AccountDuplicationResult();
    }


    @Override
    public void run() {
        int numOfSuccessDuplications = 0;
        int numOfErrorDuplications = 0;
        CreationCampaignsManger campaignsManager = (CreationCampaignsManger) this.campaignsManager;
        for (CampaignRetrive originalCampaign : campaignsManager.getOriginalCampaigns().subList(firstIndex, lastIndex)) {
            int countName = 1;
            for (String newCampaignDate : campaignsManager.getNewCampaignsForDates()) {
                try {
                    Campaign campaign = duplicateCampaign(originalCampaign, countName, newCampaignDate);
                    numOfSuccessDuplications++;
                    duplicationResult.getNewCampaigns().add(campaign.getName());
                    logger.info("Created a new campaign: {} for account {}, num of duplications for this thread: {}", campaign.getName(),
                            campaignsManager.account, numOfSuccessDuplications);
                } catch (Exception e){
                    duplicationResult.getFailureDuplications().add(originalCampaign.getName());
                    numOfErrorDuplications ++;
                    logger.error("Failed to duplicate the campaign: {} for account {}, num of failure duplications for this thread: {}",
                            originalCampaign.getName(), campaignsManager.account, numOfErrorDuplications, e);
                }
            }
        }
        campaignsManager.addResult(duplicationResult);
    }

    private Campaign duplicateCampaign (CampaignRetrive currentCampaign,int countName, String newCampaignDate) throws Exception{
        PromotedLinks promotedLinks = outBrainClientApi.getPromotedLinks(currentCampaign.getId());
        BudgetCreate budget = createBudget(currentCampaign.getBudget().getAmount(),
                currentCampaign.getBudget().getPacing(), currentCampaign.getBudget().getType(), newCampaignDate);
        BudgetRetreive newBudget = outBrainClientApi.createBudget(budget);
        CampaignCreate campaign = createCampaign(currentCampaign, newBudget.getId(), countName);
        try {
            CampaignRetrive newCampaign = outBrainClientApi.createCampaign(campaign);
            promotedLinks.getPromotedLinks().forEach(p -> {
                        PromotedLink promotedLink = createPromotedLink(p);
                        outBrainClientApi.createPromotedLink(newCampaign.getId(), promotedLink);
                    }
            );
            return newCampaign;
        }catch(Exception e){
            throw e;
        }
    }

    private BudgetCreate createBudget(Double amount, String pacing, String type, String newCampaignDate) {
        BudgetCreate budgetCreate = new BudgetCreate();
        String budgetName = UUID.randomUUID().toString().replace("-", "");
        budgetCreate.setName(budgetName);
        budgetCreate.setRunForever(true);

        budgetCreate.setAmount(amount);
        budgetCreate.setStartDate(newCampaignDate);
        budgetCreate.setPacing(pacing);
        budgetCreate.setType(type);

        return budgetCreate;
    }

    private CampaignCreate createCampaign(CampaignRetrive otherCampaign, String budgetId, int countName){
        CampaignCreate campaign = new CampaignCreate();
        Targeting originalTargeting = otherCampaign.getTargeting();

        TargetingCreate targeting = createTargeting((TargetingRetrive) originalTargeting);

        List<String> feeds = otherCampaign.getFeeds();
        campaign.setBudgetId(budgetId);
        String newName = createNewName(otherCampaign.getName(), countName);
        campaign.setName(newName);
        campaign.setEnabled(otherCampaign.getEnabled());
        campaign.setCpc(otherCampaign.getCpc());
        campaign.setTargeting(targeting);
        campaign.setSuffixTrackingCode(createSuffixTrackingCode(otherCampaign.getName(), otherCampaign.getSuffixTrackingCode(), newName));
        campaign.setFeeds(feeds);
        campaign.setOnAirType(otherCampaign.getOnAirType());
        campaign.setObjective(otherCampaign.getObjective());


        campaign.setCampaignOptimization(otherCampaign.getCampaignOptimization());

        campaign.setCreativeFormat(otherCampaign.getCreativeFormat());

        return campaign;
    }

    private String createSuffixTrackingCode(String currentCampaignName, String currentSuffixTrackingCode, String newName) {
//        String currentName = currentCampaignName.replaceAll("\\s+","");
        int firstIndex = currentSuffixTrackingCode.indexOf(currentCampaignName);
        int lastCharIndex = firstIndex + currentCampaignName.length();
        String s = "";
        try {
            s = currentSuffixTrackingCode.substring(0, firstIndex) + newName + currentSuffixTrackingCode.substring(lastCharIndex);
        } catch (Exception e) {
            logger.error("error currentSuffixTrackingCode: {}, with currentCampaignName {}", currentSuffixTrackingCode, currentCampaignName, e);
        }
        return s;
    }

    public String createNewName(String currentName , int countName) {
        int newNumber;
        int currentNumber = 0;
        int numOfDigit = getNumOfDigit(currentName);
        String substring = currentName.substring(0, currentName.length() - numOfDigit);
        if (numOfDigit == 0){
            substring += "*";
        }else{
            try {
                currentNumber = Integer.parseInt(currentName.substring(currentName.length() - numOfDigit));
            } catch (NumberFormatException e) {
                logger.error("error int name ...", e);
            }
        }
        newNumber = currentNumber + countName;
        String newName = substring + newNumber;
        int anotherCount = 1;
        CreationCampaignsManger campaignsManager = (CreationCampaignsManger) this.campaignsManager;
        while (campaignsManager.getCampaignNames().contains(newName)){
            int number = newNumber + anotherCount;
            newName = substring + number;
            anotherCount ++;
        }
        campaignsManager.addCampaignName(newName);
        return newName;
    }

    private int getNumOfDigit(String currentName) {
        int numOfDigit = 1;
        while (numOfDigit <= 5){
            String lastChar = currentName.substring(currentName.length() - numOfDigit, currentName.length() - numOfDigit + 1);
            try {
                Integer.parseInt(lastChar);
            } catch (NumberFormatException e) {
                return numOfDigit - 1;
            }
            if (lastChar.equals("*")){
                return numOfDigit - 1;
            }
            numOfDigit++;
        }

        return 0;
    }

    private PromotedLink createPromotedLink(PromotedLink otherPromotedLink){

        PromotedLink promotedLink = new PromotedLink();
        promotedLink.setText(otherPromotedLink.getText());
        String newUrl = extractUrl(otherPromotedLink.getUrl());
        promotedLink.setUrl(newUrl);
        promotedLink.setSectionName(otherPromotedLink.getSectionName());
        promotedLink.setEnabled(otherPromotedLink.getEnabled());
        OutBrainImageMetadata imageMetadata = new OutBrainImageMetadata();
        imageMetadata.setUrl(otherPromotedLink.getCachedImageUrl());
        imageMetadata.setRequestedImageUrl(otherPromotedLink.getCachedImageUrl());
        imageMetadata.setOriginalImageUrl(otherPromotedLink.getCachedImageUrl());

        promotedLink.setImageMetadata(imageMetadata);

        return promotedLink;
    }

    private String extractUrl(String url) {
        int i = url.indexOf("?");
        return url.substring(0, i);
    }

    private TargetingCreate createTargeting(TargetingRetrive originTargeting) {

        List<Location> locations1 = originTargeting.getLocations();
        String id = locations1.get(0).getId();

        TargetingCreate targeting = new TargetingCreate();
        targeting.setPlatform(originTargeting.getPlatform());

        targeting.setBrowsers(originTargeting.getBrowsers());

        List<String> locations = Collections.singletonList(id);
        targeting.setLocations(locations);

        targeting.setOperatingSystems(originTargeting.getOperatingSystems());

        targeting.setExcludeAdBlockUsers(originTargeting.getExcludeAdBlockUsers());
        return targeting;
    }
}
