//package com.example.outbrainApi.outbrain.api;
//
//import com.example.outbrainApi.outbrain.dto.*;
//import com.example.outbrainApi.service.CampaignService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//public class CampaignDuplicator extends Thread implements Runnable  {
//
//    private OutBrainApi outBrainApi;
//    private AccountDuplicationMonitor accountDuplicationMonitor;
//    private List<CampaignRetrive> originalCampaigns;
//    private List<Campaign> newCampaigns;
//    private String pattern = "HH:mm:ss";
//    private DateFormat df = new SimpleDateFormat(pattern);
//    private OutBrainClientApi outBrainClientApi;
//    private Logger logger = LoggerFactory.getLogger(CampaignDuplicator.class);
//
//    public CampaignDuplicator(String token, String account, AccountDuplicationMonitor accountDuplicationMonitor, List<CampaignRetrive> originalCampaigns) {
//        this.accountDuplicationMonitor = accountDuplicationMonitor;
//        this.originalCampaigns = originalCampaigns;
//        this.newCampaigns = new ArrayList<>();
//        this.outBrainClientApi = new OutBrainClientImpl(token, account);
//    }
//
//    @Override
//    public void run() {
//        int numOfDuplication = 0;
//        for (CampaignRetrive originalCampaign : originalCampaigns) {
//            int countName = 1;
//            for (String newCampaignDate : accountDuplicationMonitor.getNewCampaignForDates()) {
//                Campaign campaign = duplicateCampaign(originalCampaign, countName, newCampaignDate);
//                logger.info("Thread id: {} ({}), created campaign: {}",this.getId(), numOfDuplication, campaign.getName());
//                numOfDuplication++;
//                newCampaigns.add(campaign);
//            }
//        }
//        this.accountDuplicationMonitor.finishDuplicationWorking();
//    }
//
//    private Campaign duplicateCampaign (CampaignRetrive currentCampaign,int countName, String newCampaignDate){
//        PromotedLinks promotedLinks = accountDuplicationMonitor.getOutBrainClientImpl().getPromotedLinks(currentCampaign.getId());
//        BudgetCreate budget = createBudget(currentCampaign.getBudget().getAmount(),
//                currentCampaign.getBudget().getPacing(), currentCampaign.getBudget().getType(), newCampaignDate);
//        BudgetRetreive newBudget = outBrainClientApi.createBudget(budget);
//        CampaignCreate campaign = createCampaign(currentCampaign, newBudget.getId(), countName);
//        CampaignRetrive newCampaign = outBrainClientApi.createCampaign(campaign);
//        promotedLinks.getPromotedLinks().forEach(p -> {
//                    PromotedLink promotedLink = createPromotedLink(p);
//                    outBrainClientApi.createPromotedLink(newCampaign.getId(), promotedLink);
//                }
//        );
//        return newCampaign;
//    }
//
//    public OutBrainApi getOutBrainApi() {
//        return outBrainApi;
//    }
//
//    public void setOutBrainApi(OutBrainApi outBrainApi) {
//        this.outBrainApi = outBrainApi;
//    }
//
//    public AccountDuplicationMonitor getAccountDuplicationMonitor() {
//        return accountDuplicationMonitor;
//    }
//
//    public void setAccountDuplicationMonitor(AccountDuplicationMonitor accountDuplicationMonitor) {
//        this.accountDuplicationMonitor = accountDuplicationMonitor;
//    }
//
//    public List<CampaignRetrive> getOriginalCampaigns() {
//        return originalCampaigns;
//    }
//
//    public void setOriginalCampaigns(List<CampaignRetrive> originalCampaigns) {
//        this.originalCampaigns = originalCampaigns;
//    }
//
//    public List<Campaign> getNewCampaigns() {
//        return newCampaigns;
//    }
//
//    public void setNewCampaigns(List<Campaign> newCampaigns) {
//        this.newCampaigns = newCampaigns;
//    }
//
//    private BudgetCreate createBudget(Double amount, String pacing, String type, String newCampaignDate) {
//        BudgetCreate budgetCreate = new BudgetCreate();
//        String budgetName = UUID.randomUUID().toString().replace("-", "");
//        budgetCreate.setName(budgetName);
//        budgetCreate.setRunForever(true);
//
//        budgetCreate.setAmount(amount);
//        budgetCreate.setStartDate(newCampaignDate);
//        budgetCreate.setPacing(pacing);
//        budgetCreate.setType(type);
//
//        return budgetCreate;
//    }
//
//    private CampaignCreate createCampaign(CampaignRetrive otherCampaign, String budgetId, int countName){
//        CampaignCreate campaign = new CampaignCreate();
//        Targeting originalTargeting = otherCampaign.getTargeting();
//
//        TargetingCreate targeting = createTargeting((TargetingRetrive) originalTargeting);
//
//        List<String> feeds = otherCampaign.getFeeds();
//        campaign.setBudgetId(budgetId);
//        String newName = createNewName(otherCampaign.getName(), countName);
//        campaign.setName(newName);
//        campaign.setEnabled(otherCampaign.getEnabled());
//        campaign.setCpc(otherCampaign.getCpc());
//        campaign.setTargeting(targeting);
//        campaign.setSuffixTrackingCode(createSuffixTrackingCode(otherCampaign.getName(), otherCampaign.getSuffixTrackingCode(), newName));
//        campaign.setFeeds(feeds);
//        campaign.setOnAirType(otherCampaign.getOnAirType());
//        campaign.setObjective(otherCampaign.getObjective());
//
//
//        campaign.setCampaignOptimization(otherCampaign.getCampaignOptimization());
//
//        campaign.setCreativeFormat(otherCampaign.getCreativeFormat());
//
//        return campaign;
//    }
//
//    private String createSuffixTrackingCode(String currentCampaignName, String currentSuffixTrackingCode, String newName) {
//        int firstIndex = currentSuffixTrackingCode.indexOf(currentCampaignName);
//        int lastCharIndex = firstIndex + currentCampaignName.length();
//        return currentSuffixTrackingCode.substring(0, firstIndex) + newName + currentSuffixTrackingCode.substring(lastCharIndex);
//    }
//
//    public String createNewName(String currentName , int countName) {
//        int newNumber;
//        int currentNumber = 0;
//        int numOfDigit = getNumOfDigit(currentName);
//        String substring = currentName.substring(0, currentName.length() - numOfDigit);
//        if (numOfDigit == 0){
//            substring += "*";
//        }else{
//            try {
//                currentNumber = Integer.parseInt(currentName.substring(currentName.length() - numOfDigit));
//            } catch (NumberFormatException e) {
//                logger.error("error int name ...", e);
//            }
//        }
//        newNumber = currentNumber + countName;
//        String newName = substring + newNumber;
//        int anotherCount = 1;
//        while (accountDuplicationMonitor.getCampaignNames().contains(newName)){
//            int number = newNumber + anotherCount;
//            newName = substring + number;
//            anotherCount ++;
//        }
//        accountDuplicationMonitor.addCampaignName(newName);
//        return newName;
//    }
//
//    private int getNumOfDigit(String currentName) {
//        int numOfDigit = 1;
//        while (numOfDigit <= 5){
//            String lastChar = currentName.substring(currentName.length() - numOfDigit, currentName.length() - numOfDigit + 1);
//            try {
//                Integer.parseInt(lastChar);
//            } catch (NumberFormatException e) {
//                return numOfDigit - 1;
//            }
//            if (lastChar.equals("*")){
//                return numOfDigit - 1;
//            }
//            numOfDigit++;
//        }
//
//        return 0;
//    }
//
//    private PromotedLink createPromotedLink(PromotedLink otherPromotedLink){
//
//        PromotedLink promotedLink = new PromotedLink();
//        promotedLink.setText(otherPromotedLink.getText());
//        String newUrl = extractUrl(otherPromotedLink.getUrl());
//        promotedLink.setUrl(newUrl);
//        promotedLink.setSectionName(otherPromotedLink.getSectionName());
//        promotedLink.setEnabled(otherPromotedLink.getEnabled());
//        OutBrainImageMetadata imageMetadata = new OutBrainImageMetadata();
//        imageMetadata.setUrl(otherPromotedLink.getCachedImageUrl());
//        imageMetadata.setRequestedImageUrl(otherPromotedLink.getCachedImageUrl());
//        imageMetadata.setOriginalImageUrl(otherPromotedLink.getCachedImageUrl());
//
//        promotedLink.setImageMetadata(imageMetadata);
//
//        return promotedLink;
//    }
//
//    private String extractUrl(String url) {
//        int i = url.indexOf("?");
//        return url.substring(0, i);
//    }
//
//    private TargetingCreate createTargeting(TargetingRetrive originTargeting) {
//
//        List<Location> locations1 = originTargeting.getLocations();
//        String id = locations1.get(0).getId();
//
//        TargetingCreate targeting = new TargetingCreate();
//        targeting.setPlatform(originTargeting.getPlatform());
//
//        targeting.setBrowsers(originTargeting.getBrowsers());
//
//        List<String> locations = Collections.singletonList(id);
//        targeting.setLocations(locations);
//
//        targeting.setOperatingSystems(originTargeting.getOperatingSystems());
//
//        targeting.setExcludeAdBlockUsers(originTargeting.getExcludeAdBlockUsers());
//        return targeting;
//    }
//}
