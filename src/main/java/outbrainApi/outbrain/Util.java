package outbrainApi.outbrain;

import com.example.outbrainApi.model.Parameter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
    /**
     * Append parameters to given url
     * @param url
     * @param parameters
     * @return new String url with given parameters
     * @throws URISyntaxException
     */
    public static String appendToUrl(String url, List<Parameter> parameters) throws URISyntaxException
    {
        URI uri = new URI(url);
        String query = uri.getQuery();

        StringBuilder builder = new StringBuilder();

        if (query != null)
            builder.append(query);


        for (Parameter parameter : parameters) {
            String keyValueParam = parameter.getKey() + "=" + parameter.getValue();
            if (!builder.toString().isEmpty())
                builder.append("&");

            builder.append(keyValueParam);
        }

        return uri + builder.toString();
    }
}
