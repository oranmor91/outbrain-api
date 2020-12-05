package com.example.outbrainApi.outbrain.api;

import com.example.outbrainApi.outbrain.Urls;
import com.example.outbrainApi.outbrain.Util;
import com.example.outbrainApi.outbrain.dto.Campaign;
import com.example.outbrainApi.outbrain.dto.CampaignRetrive;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AccountDuplicationMonitor {
    private Client client;
    private ObjectMapper mapper;
    private String token;
    private String account;
    private List<CampaignRetrive> originalCampaigns;
    private Set<String> campaignNames;
    private List<String> newCampaignForDates;
    private int numOfDuplicationCampaignsForThread;
    private int numOfTotalThreads;
    private int numOfThreadsAreDone;
    private final String OB_TOKEN_V1 = "OB-TOKEN-V1";


    public AccountDuplicationMonitor(int numOfTotalThreads, String token, String account, List<String> lastDuplicationDates, List<String> newCampaignForDates) {
        this.client = ClientBuilder.newClient();
        this.mapper = new ObjectMapper();
        this.numOfTotalThreads = numOfTotalThreads;
        this.token = token;
        this.account = account;
        this.originalCampaigns = new ArrayList<>();
        this.campaignNames = new HashSet<>();
        for (String lastDuplicationDate : lastDuplicationDates) {
            CampaignsByDate campaignsByDate = getCampaignsByDate(lastDuplicationDate);
            this.originalCampaigns.addAll(campaignsByDate.getRelevantCampaigns());
            this.campaignNames.addAll(campaignsByDate.getCampaignNames());
        }
        this.newCampaignForDates = newCampaignForDates;
        assert originalCampaigns != null;
        this.numOfDuplicationCampaignsForThread = (originalCampaigns.size() / numOfTotalThreads) + 1;
        this.numOfThreadsAreDone = 0;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<CampaignRetrive> getOriginalCampaigns() {
        return originalCampaigns;
    }

    public void setOriginalCampaigns(List<CampaignRetrive> originalCampaigns) {
        this.originalCampaigns = originalCampaigns;
    }

    public List<String> getNewCampaignForDates() {
        return newCampaignForDates;
    }

    public void setNewCampaignForDates(List<String> newCampaignForDates) {
        this.newCampaignForDates = newCampaignForDates;
    }

    public synchronized Set<String> getCampaignNames() {
        return campaignNames;
    }

    public synchronized void setCampaignNames(Set<String> campaignNames) {
        this.campaignNames = campaignNames;
    }

    public int getNumOfDuplicationCampaignsForThread() {
        return numOfDuplicationCampaignsForThread;
    }

    public void setNumOfDuplicationCampaignsForThread(int numOfDuplicationCampaignsForThread) {
        this.numOfDuplicationCampaignsForThread = numOfDuplicationCampaignsForThread;
    }

    public synchronized boolean isAllThreadsAreDone() {
        return numOfThreadsAreDone == numOfTotalThreads ;
    }

    public synchronized void finishDuplicationWorking() {
        numOfThreadsAreDone ++;
    }

    private CampaignsByDate getCampaignsByDate(String lastDuplicationDate) {
        String pattern = "HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        List<CampaignRetrive> allCampaigns = new ArrayList<>();
        int offset = 0;
        CampaignResponse campaignResponse = getCampaignsResponse(offset);
        allCampaigns.addAll(campaignResponse.getCampaigns());
        offset += campaignResponse.getCount();
        int totalCount = campaignResponse.getTotalCount();
        System.out.println("Start collecting " + totalCount + " campaigns");
        System.out.println("--------------------------------------------------------------------");
        while (offset < totalCount){
            CampaignResponse response = getCampaignsResponse(offset);
            allCampaigns.addAll(response.getCampaigns());
            Date collectTime = new Date();
            String collectTimeString = df.format(collectTime);
            offset += campaignResponse.getCount();
            System.out.println("Collect " + offset + " campaigns " + collectTimeString);
        }

        Set<String> campaignNames = allCampaigns.stream().map(Campaign::getName).collect(Collectors.toSet());

        List<CampaignRetrive> relevantCampaigns =
                allCampaigns.stream()
                        .filter(campaignRetrive -> getDateByString(campaignRetrive.getBudget().getStartDate()).equals(lastDuplicationDate) &&
                                !campaignRetrive.getLiveStatus().getOnAirReason().equals("CAMPAIGN_DISABLED") &&
                                !campaignRetrive.getLiveStatus().getOnAirReason().equals("PAUSED"))
                        .collect(Collectors.toList());

        CampaignsByDate campaignsByDate = new CampaignsByDate(campaignNames, relevantCampaigns);
        return campaignsByDate;
    }

//    private CampaignsByDate getCampaignsByDate2(String lastDuplicationDate) {
//        String pattern = "HH:mm:ss";
//        DateFormat df = new SimpleDateFormat(pattern);
//        List<CampaignRetrive> allCampaigns = new ArrayList<>();
//        CampaignResponse campaignResponse = getCampaignsResponse(0);
//        int totalCount = campaignResponse.getTotalCount();
//
//        int offset = 0;
//        int numOfCampaignForRetrive = totalCount / 10;
//        while (offset <= totalCount){
//            int lastOffset =
//                    Math.min(offset + numOfCampaignForRetrive, totalCount);
//            CampaignRetriever campaignRetriever = new CampaignRetriever(this, offset, lastOffset);
//            campaignDuplicators.add(campaignRetriever);
//            Thread thread = new Thread(campaignRetriever);
//            thread.start();
//            offset += numOfCampaignForRetrive;
//        }
//
//
//        while (!accountDuplicationMonitor.isAllThreadsAreDone()){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//        allCampaigns.addAll(campaignResponse.getCampaigns());
//        offset += campaignResponse.getCount();
//        System.out.println("Start collecting " + totalCount + " campaigns");
//        System.out.println("--------------------------------------------------------------------");
//        while (offset < totalCount){
//            CampaignResponse response = getCampaignsResponse(offset);
//            allCampaigns.addAll(response.getCampaigns());
//            Date collectTime = new Date();
//            String collectTimeString = df.format(collectTime);
//            offset += campaignResponse.getCount();
//            System.out.println("Collect " + offset + " campaigns " + collectTimeString);
//        }
//
//        Set<String> campaignNames = allCampaigns.stream().map(Campaign::getName).collect(Collectors.toSet());
//
//        List<CampaignRetrive> relevantCampaigns =
//                allCampaigns.stream()
//                        .filter(campaignRetrive -> getDateByString(campaignRetrive.getBudget().getStartDate()).equals(lastDuplicationDate) &&
//                                !campaignRetrive.getLiveStatus().getOnAirReason().equals("CAMPAIGN_DISABLED") &&
//                                !campaignRetrive.getLiveStatus().getOnAirReason().equals("PAUSED"))
//                        .collect(Collectors.toList());
//
//        CampaignsByDate campaignsByDate = new CampaignsByDate(campaignNames, relevantCampaigns);
//        return campaignsByDate;
//    }

    private CampaignResponse getCampaignsResponse(int offset) {
        String retriveCampaignsUrl =
                String.format(Urls.RETRIVE_CAMPAIGNS_URL, account);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("includeArchived", "true");
        parameters.put("fetch", "all");
        parameters.put("extraFields", "2CCampaignOptimization");
        parameters.put("limit", "50");
        parameters.put("offset", String.valueOf(offset));

        String url = "";
        try {
            url = Util.appendToUrl(retriveCampaignsUrl, parameters);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header(OB_TOKEN_V1, token)
                .get();

        String campaignsInJsonString = response.readEntity(String.class);
        CampaignResponse campaignResponse = null;
        try {
            campaignResponse = mapper.readValue(campaignsInJsonString, CampaignResponse.class);
        } catch (IOException e) {
            System.out.println("error to get campaign: " + e + " " +"campaignsInJsonString:" +  campaignsInJsonString);
        }
        return campaignResponse;
    }

    private String getDateByString(Date campaignDate) {
        String pattern = "yyyy-MM-dd";

        DateFormat df = new SimpleDateFormat(pattern);

        return df.format(campaignDate);
    }

    private CampaignRetrive getCampaign(String campaignId) {

        String retriveCampaignUrl = String.format(Urls.RETRIVE_CAMPAIGN_URL, campaignId);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("extraFields", "Locations&CampaignOptimization");

        String url = "";
        try {
            url = Util.appendToUrl(retriveCampaignUrl, parameters);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header(OB_TOKEN_V1, token)
                .get();

        String campaignInJsonString = response.readEntity(String.class);
        CampaignRetrive campaign = null;
        try {
            campaign = mapper.readValue(campaignInJsonString, CampaignRetrive.class);
        } catch (IOException e) {
            System.out.println("error to get campaign: " + e + " " +"campaignInJsonString:" +  campaignInJsonString);
        }
        return campaign;
    }

    public String getToken() {
        return token;
    }

    public synchronized void addCampaignName(String newName) {
        campaignNames.add(newName);
    }
}
