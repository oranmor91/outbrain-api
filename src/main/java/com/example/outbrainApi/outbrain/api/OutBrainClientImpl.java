package com.example.outbrainApi.outbrain.api;

import com.example.outbrainApi.model.Parameter;
import com.example.outbrainApi.outbrain.Util;
import com.example.outbrainApi.outbrain.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class OutBrainClientImpl implements OutBrainClientApi {

    private String token;
    private Client client;
    private ObjectMapper mapper;
    private String account;
    private final String OB_TOKEN_V1 = "OB-TOKEN-V1";
    private Logger logger = LoggerFactory.getLogger(OutBrainClientImpl.class);


    private final String RETRIVE_CAMPAIGNS_URL = "https://api.outbrain.com/amplify/v0.1/marketers/%s/campaigns?";
    private static final String RETRIVE_CAMPAIGN_URL = "https://api.outbrain.com/amplify/v0.1/campaigns/%s?";
    private static final String RETRIVE_PROMOTEDLINKS_URL = "https://api.outbrain.com/amplify/v0.1/campaigns/%s/promotedLinks?";
    private static final String CREATE_BUDGET_URL = "https://api.outbrain.com/amplify/v0.1/marketers/%s/budgets?";
    private static final String CREATE_CAMPAIGN_URL = "https://api.outbrain.com/amplify/v0.1/campaigns?";
    private static final String CREATE_PROMOTED_LINK_URL = "https://api.outbrain.com/amplify/v0.1/campaigns/%s/promotedLinks";


    public OutBrainClientImpl(String token, String account) {
        this.client = ClientBuilder.newClient();
        this.mapper = new ObjectMapper();
        this.token = token;
        this.account = account;
    }

    public CampaignResponse getCampaigns(int offset, int limit){
        String retriveCampaignsUrl =
                String.format(RETRIVE_CAMPAIGNS_URL, account);

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("includeArchived", "true"));
        parameters.add(new Parameter("fetch", "all"));
        parameters.add(new Parameter("extraFields", "Locations%2CCampaignOptimization"));
        parameters.add(new Parameter("limit", String.valueOf(limit)));
        parameters.add(new Parameter("offset", String.valueOf(offset)));

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
            logger.error("error to get campaign: {}", campaignsInJsonString, e);
            e.printStackTrace();
        }
        return campaignResponse;
    }

    public CampaignRetrive getCampaign(String campaignId) {

        String retriveCampaignUrl = String.format(RETRIVE_CAMPAIGN_URL, campaignId);

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("extraFields", "Locations&CampaignOptimization"));

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
            logger.error("error to get campaign: {}", campaignInJsonString, e);
            e.printStackTrace();
        }
        return campaign;
    }

    public BudgetRetreive createBudget(BudgetCreate budget) throws Exception{
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json ;
        try {
            json = ow.writeValueAsString(budget);
        } catch (IOException e) {
            logger.error("error to get budgetCreate: {}", budget, e);
            throw e;
        }
        Entity payload = Entity.json(json);


        String createBudgetUrl = String.format(OutBrainClientImpl.CREATE_BUDGET_URL, account);

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("detachedOnly", "detachedOnly"));

        String url = "";
        try {
            url = Util.appendToUrl(createBudgetUrl, parameters);
        } catch (URISyntaxException e) {
            throw e;
        }

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(OB_TOKEN_V1, token)
                .post(payload);


        String budgetInJsonString = response.readEntity(String.class);
        BudgetRetreive budgetRetreive;
        try {
            budgetRetreive = mapper.readValue(budgetInJsonString, BudgetRetreive.class);
        } catch (IOException e) {
            logger.error("error to get budget: {}", budgetInJsonString, e);
            throw e;
        }
        return budgetRetreive;
    }

    public PromotedLinks getPromotedLinks(String campaignId) throws Exception{
        String retrivePromotedLinks = String.format(OutBrainClientImpl.RETRIVE_PROMOTEDLINKS_URL, campaignId);

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("enabled", "true"));
        parameters.add(new Parameter("limit", "100"));
        parameters.add(new Parameter("offset", "0"));
        parameters.add(new Parameter("sort", "-creationTime"));
        parameters.add(new Parameter("promotedLinkImageWidth", "100"));
        parameters.add(new Parameter("promotedLinkImageHeight", "100"));
        parameters.add(new Parameter("extraFields", "ImageMetaData"));

        String url = "";
        try {
            url = Util.appendToUrl(retrivePromotedLinks, parameters);
        } catch (URISyntaxException e) {
            throw e;
        }

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header(OB_TOKEN_V1, token)
                .get();

        String promotedLinksInJsonString = response.readEntity(String.class);
        PromotedLinks promotedLinks = null;
        try {
            promotedLinks = mapper.readValue(promotedLinksInJsonString, PromotedLinks.class);
        } catch (IOException e) {
            logger.error("error to get promotedLinks: {}", promotedLinksInJsonString, e);
            throw e;
        }
        return promotedLinks;
    }

    public CampaignRetrive createCampaign(CampaignCreate campaign) throws URISyntaxException, JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(campaign);
        } catch (IOException e) {
            logger.error("error to get campaign: {}", campaign, e);
            throw e;
        }
        Entity<String> payload = Entity.json(json);

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("extraFields", "Locations"));

        String url = "";
        try {
            url = Util.appendToUrl(OutBrainClientImpl.CREATE_CAMPAIGN_URL, parameters);
        } catch (URISyntaxException e) {
            logger.error("ass something");
            throw e;
        }

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(OB_TOKEN_V1, token)
                .post(payload);
        String campaigntInJsonString = response.readEntity(String.class);
        CampaignRetrive campaignRetreive;
        try {
            campaignRetreive = mapper.readValue(campaigntInJsonString, CampaignRetrive.class);
        } catch (IOException e) {
            logger.error("error to get campaign: {}", campaign, e);
            throw e;
        }
        return campaignRetreive;
    }

    public PromotedLink createPromotedLink(String campaignId, PromotedLink promotedLink){
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(promotedLink);
        } catch (IOException e) {
            logger.error("error to get promotedLinks: {}", promotedLink, e);
        }
        Entity payload = Entity.json(json);
        String url = String.format(OutBrainClientImpl.CREATE_PROMOTED_LINK_URL, campaignId);
        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(OB_TOKEN_V1, token)
                .post(payload);
        String promotedLinkInJsonString = response.readEntity(String.class);
        PromotedLink prmotdLink = null;
        try {
            prmotdLink = mapper.readValue(promotedLinkInJsonString, PromotedLink.class);
        } catch (IOException e) {
            logger.error("error to get promotedLinks: {}", promotedLink, e);
        }
        return prmotdLink;
    }
}
