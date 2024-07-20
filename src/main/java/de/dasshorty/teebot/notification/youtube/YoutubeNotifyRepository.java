package de.dasshorty.teebot.notification.youtube;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface YoutubeNotifyRepository extends MongoRepository<YoutubeNotifyDto, String> {
}
