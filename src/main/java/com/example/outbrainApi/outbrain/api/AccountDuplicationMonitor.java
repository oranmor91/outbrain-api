//package com.example.outbrainApi.outbrain.api;
//
//import com.example.outbrainApi.outbrain.dto.Campaign;
//import com.example.outbrainApi.outbrain.dto.CampaignRetrive;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class AccountDuplicationMonitor {
//    private List<CampaignRetrive> originalCampaigns;
//    private Set<String> campaignNames;
//    private List<String> newCampaignForDates;
//    private int numOfDuplicationCampaignsForThread;
//    private int numOfTotalThreads;
//    private int numOfThreadsAreDone;
//    private OutBrainClientApi outBrainClientImpl;
//    private Logger logger = LoggerFactory.getLogger(AccountDuplicationMonitor.class);
//
//
//    public AccountDuplicationMonitor(int numOfTotalThreads, String token, String account, List<String> lastDuplicationDates, List<String> newCampaignForDates) {
//        this.outBrainClientImpl = new OutBrainClientImpl(token, account);
//        this.numOfTotalThreads = numOfTotalThreads;
//        this.originalCampaigns = new ArrayList<>();
//        this.campaignNames = new HashSet<>();
//        for (String lastDuplicationDate : lastDuplicationDates) {
//            CampaignsByDate campaignsByDate = getCampaignsByDate(lastDuplicationDate);
//            this.originalCampaigns.addAll(campaignsByDate.getRelevantCampaigns());
//            this.campaignNames.addAll(campaignsByDate.getCampaignNames());
//        }
//        this.newCampaignForDates = newCampaignForDates;
//        assert originalCampaigns != null;
//        this.numOfDuplicationCampaignsForThread = (originalCampaigns.size() / numOfTotalThreads) + 1;
//        this.numOfThreadsAreDone = 0;
//    }
//
//
//    public List<CampaignRetrive> getOriginalCampaigns() {
//        return originalCampaigns;
//    }
//
//    public void setOriginalCampaigns(List<CampaignRetrive> originalCampaigns) {
//        this.originalCampaigns = originalCampaigns;
//    }
//
//    public List<String> getNewCampaignForDates() {
//        return newCampaignForDates;
//    }
//
//    public void setNewCampaignForDates(List<String> newCampaignForDates) {
//        this.newCampaignForDates = newCampaignForDates;
//    }
//
//    public synchronized Set<String> getCampaignNames() {
//        return campaignNames;
//    }
//
//    public synchronized void setCampaignNames(Set<String> campaignNames) {
//        this.campaignNames = campaignNames;
//    }
//
//    public int getNumOfDuplicationCampaignsForThread() {
//        return numOfDuplicationCampaignsForThread;
//    }
//
//    public void setNumOfDuplicationCampaignsForThread(int numOfDuplicationCampaignsForThread) {
//        this.numOfDuplicationCampaignsForThread = numOfDuplicationCampaignsForThread;
//    }
//
//    public synchronized boolean isAllThreadsAreDone() {
//        return numOfThreadsAreDone == numOfTotalThreads ;
//    }
//
//    public synchronized void finishDuplicationWorking() {
//        numOfThreadsAreDone ++;
//    }
//
//    private CampaignsByDate getCampaignsByDate(String lastDuplicationDate) {
//        String pattern = "HH:mm:ss";
//        List<CampaignRetrive> allCampaigns = new ArrayList<>();
//        int offset = 0;
//        CampaignResponse campaignResponse = outBrainClientImpl.getCampaigns(offset);
//        allCampaigns.addAll(campaignResponse.getCampaigns());
//        offset += campaignResponse.getCount();
//        int totalCount = campaignResponse.getTotalCount();
//        logger.info("Start collecting {} campaigns", totalCount);
//        while (offset < totalCount){
//            CampaignResponse response = outBrainClientImpl.getCampaigns(offset);
//            allCampaigns.addAll(response.getCampaigns());
//            offset += campaignResponse.getCount();
//            logger.info("Collect {} campaigns", offset);
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
//
//
//    private String getDateByString(Date campaignDate) {
//        String pattern = "yyyy-MM-dd";
//
//        DateFormat df = new SimpleDateFormat(pattern);
//
//        return df.format(campaignDate);
//    }
//
//    public synchronized void addCampaignName(String newName) {
//        campaignNames.add(newName);
//    }
//
//    public OutBrainClientApi getOutBrainClientImpl() {
//        return outBrainClientImpl;
//    }
//}
