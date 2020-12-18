package outbrainApi.outbrain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignCreate extends Campaign {

        private TargetingCreate targeting;

        public TargetingCreate getTargeting() {
                return targeting;
        }

        public void setTargeting(TargetingCreate targeting) {
                this.targeting = targeting;
        }
}
