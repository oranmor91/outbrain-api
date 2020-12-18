package outbrainApi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CampaignsDuplication {
    private String account;
    private List<String> lastDuplicationDates;
    private List<String> newCampaignForDates;

    public CampaignsDuplication(@JsonProperty("account") String account,
                                @JsonProperty("lastDuplicationDates") List<String> lastDuplicationDates,
                                @JsonProperty("newCampaignForDates") List<String> newCampaignForDates) {
        this.account = account;
        this.lastDuplicationDates = lastDuplicationDates;
        this.newCampaignForDates = newCampaignForDates;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<String> getLastDuplicationDates() {
        return lastDuplicationDates;
    }

    public void setLastDuplicationDates(List<String> lastDuplicationDates) {
        this.lastDuplicationDates = lastDuplicationDates;
    }

    public List<String> getNewCampaignForDates() {
        return newCampaignForDates;
    }

    public void setNewCampaignForDates(List<String> newCampaignForDates) {
        this.newCampaignForDates = newCampaignForDates;
    }
}
