package com.example.outbrainApi.outbrain.api;

import com.example.outbrainApi.outbrain.Urls;
import com.example.outbrainApi.outbrain.Util;
import com.example.outbrainApi.outbrain.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CampaignDuplicator extends Thread implements Runnable  {

    private Client client;
    private ObjectMapper mapper;
    private OutBrainApi outBrainApi;
    private AccountDuplicationMonitor accountDuplicationMonitor;
    private List<CampaignRetrive> originalCampaigns;
    private List<Campaign> newCampaigns;
    private final String OB_TOKEN_V1 = "OB-TOKEN-V1";
    String pattern = "HH:mm:ss";
    DateFormat df = new SimpleDateFormat(pattern);

    public CampaignDuplicator(AccountDuplicationMonitor accountDuplicationMonitor, List<CampaignRetrive> originalCampaigns) {
        this.client = ClientBuilder.newClient();
        this.mapper = new ObjectMapper();
        this.accountDuplicationMonitor = accountDuplicationMonitor;
        this.originalCampaigns = originalCampaigns;
        this.newCampaigns = new ArrayList<>();
    }

    @Override
    public void run() {
        int numOfDuplication = 0;
        for (CampaignRetrive originalCampaign : originalCampaigns) {
            int countName = 1;
            for (String newCampaignDate : accountDuplicationMonitor.getNewCampaignForDates()) {
                Campaign campaign = duplicateCampaign(originalCampaign, countName, newCampaignDate);
                Date now = new Date();
                String time = df.format(now);
                System.out.println("Thread id: " + this.getId() + "(" + numOfDuplication + ")" + " Created campaign: " + campaign.getName() + " ,creation time: " + time);
                numOfDuplication++;
                newCampaigns.add(campaign);
            }
        }
        this.accountDuplicationMonitor.finishDuplicationWorking();
    }

    private Campaign duplicateCampaign (CampaignRetrive currentCampaign,int countName, String newCampaignDate){
        PromotedLinks promotedLinks = getPromotedLinks(currentCampaign.getId());
        Campaign newCampaign = createCampaign(currentCampaign, countName, newCampaignDate);
        for (PromotedLink promotedLink : promotedLinks.getPromotedLinks()) {
            createPromotedLink(newCampaign.getId(), promotedLink);
        }
        return newCampaign;
    }

    public OutBrainApi getOutBrainApi() {
        return outBrainApi;
    }

    public void setOutBrainApi(OutBrainApi outBrainApi) {
        this.outBrainApi = outBrainApi;
    }

    public AccountDuplicationMonitor getAccountDuplicationMonitor() {
        return accountDuplicationMonitor;
    }

    public void setAccountDuplicationMonitor(AccountDuplicationMonitor accountDuplicationMonitor) {
        this.accountDuplicationMonitor = accountDuplicationMonitor;
    }

    public List<CampaignRetrive> getOriginalCampaigns() {
        return originalCampaigns;
    }

    public void setOriginalCampaigns(List<CampaignRetrive> originalCampaigns) {
        this.originalCampaigns = originalCampaigns;
    }

    public List<Campaign> getNewCampaigns() {
        return newCampaigns;
    }

    public void setNewCampaigns(List<Campaign> newCampaigns) {
        this.newCampaigns = newCampaigns;
    }

    private Budget createBudget(Double amount, String pacing, String type, String newCampaignDate) {
        BudgetCreate budgetCreate = new BudgetCreate();
        String budgetName = UUID.randomUUID().toString().replace("-", "");
        budgetCreate.setName(budgetName);
        budgetCreate.setRunForever(true);

        budgetCreate.setAmount(amount);
        budgetCreate.setStartDate(newCampaignDate);
        budgetCreate.setPacing(pacing);
        budgetCreate.setType(type);


        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(budgetCreate);
        } catch (IOException e) {
            System.out.println("error to get budgetCreate: " + e + " " +"budgetCreate:" +  budgetCreate);
        }
        Entity payload = Entity.json(json);

        String createBudgetUrl = String.format(Urls.CREATE_BUDGET_URL, accountDuplicationMonitor.getAccount());

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("detachedOnly", "detachedOnly");

        String url = "";
        try {
            url = Util.appendToUrl(createBudgetUrl, parameters);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(OB_TOKEN_V1, accountDuplicationMonitor.getToken())
                .post(payload);
        String budgetInJsonString = response.readEntity(String.class);
        BudgetRetreive budget = null;
        try {
            budget = mapper.readValue(budgetInJsonString, BudgetRetreive.class);
        } catch (IOException e) {
            System.out.println("error to get budget: " + e + " " +"budgetInJsonString:" +  budgetInJsonString);
        }
        return budget;
    }

    private PromotedLinks getPromotedLinks(String campaignId){
        String retrivePromotedLinks = String.format(Urls.RETRIVE_PROMOTEDLINKS_URL, campaignId);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("enabled", "true");
        parameters.put("limit", "100");
        parameters.put("offset", "0");
        parameters.put("sort", "-creationTime");
        parameters.put("promotedLinkImageWidth", "100");
        parameters.put("promotedLinkImageHeight", "100");
        parameters.put("extraFields", "ImageMetaData");

        String url = "";
        try {
            url = Util.appendToUrl(retrivePromotedLinks, parameters);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header(OB_TOKEN_V1, accountDuplicationMonitor.getToken())
                .get();

        String promotedLinksInJsonString = response.readEntity(String.class);
        PromotedLinks promotedLinks = null;
        try {
            promotedLinks = mapper.readValue(promotedLinksInJsonString, PromotedLinks.class);
        } catch (IOException e) {
            System.out.println("error to get promotedLinks: " + e + " " +"promotedLinksInJsonString:" +  promotedLinksInJsonString);
        }
        return promotedLinks;
    }

    private CampaignRetrive createCampaign(CampaignRetrive otherCampaign, int countName, String newCampaignDate){
        CampaignCreate campaign = new CampaignCreate();
        Targeting originalTargeting = otherCampaign.getTargeting();

        TargetingCreate targeting = createTargeting((TargetingRetrive) originalTargeting);

        List<String> feeds = otherCampaign.getFeeds();
        Budget budget = createBudget(otherCampaign.getBudget().getAmount(),
                otherCampaign.getBudget().getPacing(), otherCampaign.getBudget().getType(), newCampaignDate);
        campaign.setBudgetId(budget.getId());
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

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(campaign);
        } catch (IOException e) {
            System.out.println("error to get campaign: " + e + " " +"campaign:" +  campaign);
        }
        Entity<String> payload = Entity.json(json);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("extraFields", "Locations");

        String url = "";
        try {
            url = Util.appendToUrl(Urls.CREATE_CAMPAIGN_URL, parameters);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(OB_TOKEN_V1, accountDuplicationMonitor.getToken())
                .post(payload);
        String campaigntInJsonString = response.readEntity(String.class);
        CampaignRetrive campaignRetreive = null;
        try {
            campaignRetreive = mapper.readValue(campaigntInJsonString, CampaignRetrive.class);
        } catch (IOException e) {
            System.out.println("error to get campaign: " + e + " " +"campaigntInJsonString:" +  campaigntInJsonString);
        }
        return campaignRetreive;
    }

    private String createSuffixTrackingCode(String currentCampaignName, String currentSuffixTrackingCode, String newName) {
        int firstIndex = currentSuffixTrackingCode.indexOf(currentCampaignName);
        int lastCharIndex = firstIndex + currentCampaignName.length();
        return currentSuffixTrackingCode.substring(0, firstIndex) + newName + currentSuffixTrackingCode.substring(lastCharIndex);
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
                System.out.println("error int name ..." + e);
            }
        }
        newNumber = currentNumber + countName;
        String newName = substring + newNumber;
        int anotherCount = 1;
        while (accountDuplicationMonitor.getCampaignNames().contains(newName)){
            int number = newNumber + anotherCount;
            newName = substring + number;
            anotherCount ++;
        }
        accountDuplicationMonitor.addCampaignName(newName);
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

    private PromotedLink createPromotedLink(String campaignId, PromotedLink otherPromotedLink){

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


        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(promotedLink);
        } catch (IOException e) {
            System.out.println("error to get promotedLinks: " + e + " " +"promotedLink:" +  promotedLink);
        }
        Entity payload = Entity.json(json);
        String url = String.format(Urls.CREATE_PROMOTED_LINK_URL, campaignId);
        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(OB_TOKEN_V1, accountDuplicationMonitor.getToken())
                .post(payload);
        String promotedLinkInJsonString = response.readEntity(String.class);
        PromotedLink prmotdLink = null;
        try {
            prmotdLink = mapper.readValue(promotedLinkInJsonString, PromotedLink.class);
        } catch (IOException e) {
            System.out.println("error to get promotedLinks: " + e + " " +"promotedLinksInJsonString:" +  promotedLinkInJsonString);
        }
        return prmotdLink;
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
