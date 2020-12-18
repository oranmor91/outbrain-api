package outbrainApi.outbrain.api;

import com.example.outbrainApi.outbrain.dto.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class OutBrainApiImpl implements OutBrainApi {
    private String pattern = "HH:mm:ss";
    private DateFormat df = new SimpleDateFormat(pattern);
    private String token;

    public OutBrainApiImpl() {
        token = System.getenv("token");
    }

    @Override
    public List<String> duplicateCampaigns(String account, List<String> newCampaignForDates, List<String> lastDuplicationDates){
        AccountDuplicationMonitor accountDuplicationMonitor =
                new AccountDuplicationMonitor(10, token, account, lastDuplicationDates, newCampaignForDates);
        Date startTime = new Date();
        String startTimeString = df.format(startTime);
        System.out.println("Start duplication at " + startTimeString);
        System.out.println("-----------------------------------------------------------------------------------------------------");
        List<CampaignDuplicator> campaignDuplicators = new ArrayList<>();


        int index = 0;
        while (index <= accountDuplicationMonitor.getOriginalCampaigns().size()){
            int lastIndex =
                    Math.min(index + accountDuplicationMonitor.getNumOfDuplicationCampaignsForThread(), accountDuplicationMonitor.getOriginalCampaigns().size());
            CampaignDuplicator campaignDuplicator =
                    new CampaignDuplicator(token, account, accountDuplicationMonitor, accountDuplicationMonitor.getOriginalCampaigns().subList(index, lastIndex));
            campaignDuplicators.add(campaignDuplicator);
            Thread thread = new Thread(campaignDuplicator);
            thread.start();
            index += accountDuplicationMonitor.getNumOfDuplicationCampaignsForThread();
        }


        while (!accountDuplicationMonitor.isAllThreadsAreDone()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return campaignDuplicators.stream().map(CampaignDuplicator::getNewCampaigns).flatMap(Collection::stream)
                .collect(Collectors.toList()).stream().map(Campaign::getName).collect(Collectors.toList());

    }
}



