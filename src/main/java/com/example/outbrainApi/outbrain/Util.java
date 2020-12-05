package com.example.outbrainApi.outbrain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Util {
    /**
     * Append parameters to given url
     * @param url
     * @param parameters
     * @return new String url with given parameters
     * @throws URISyntaxException
     */
    public static String appendToUrl(String url, HashMap<String, String> parameters) throws URISyntaxException
    {
        URI uri = new URI(url);
        String query = uri.getQuery();

        StringBuilder builder = new StringBuilder();

        if (query != null)
            builder.append(query);

        for (Map.Entry<String, String> entry: parameters.entrySet())
        {
            String keyValueParam = entry.getKey() + "=" + entry.getValue();
            if (!builder.toString().isEmpty())
                builder.append("&");

            builder.append(keyValueParam);
        }

        URI newUri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), builder.toString(), uri.getFragment());
        return newUri.toString();
    }
}
