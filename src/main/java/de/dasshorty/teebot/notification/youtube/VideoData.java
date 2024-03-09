package de.dasshorty.teebot.notification.youtube;

public record VideoData(String title, String description, String thumbnail, String viewLink) {

    public String capDescription() {
        return this.description.substring(0, 30) + "...";
    }

}
