package de.dasshorty.teebot.jtc;

import org.springframework.data.annotation.Id;

public class JTCDto {

    public String getChannelId() {
        return this.channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelOwnerId() {
        return this.channelOwnerId;
    }

    public void setChannelOwnerId(String channelOwnerId) {
        this.channelOwnerId = channelOwnerId;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Id
    private String channelId;
    private String channelOwnerId;
    private String categoryId;

}
