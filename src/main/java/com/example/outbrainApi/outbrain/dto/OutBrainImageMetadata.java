package com.example.outbrainApi.outbrain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutBrainImageMetadata {

    private String id;
    private String requestedImageUrl;
    private String originalImageUrl;
    private String type;
    private String url;
    private String cropType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestedImageUrl() {
        return requestedImageUrl;
    }

    public void setRequestedImageUrl(String requestedImageUrl) {
        this.requestedImageUrl = requestedImageUrl;
    }

    public String getOriginalImageUrl() {
        return originalImageUrl;
    }

    public void setOriginalImageUrl(String originalImageUrl) {
        this.originalImageUrl = originalImageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }
}
