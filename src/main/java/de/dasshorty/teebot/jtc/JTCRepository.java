package de.dasshorty.teebot.jtc;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface JTCRepository extends MongoRepository<JTCDto, String> {

    Optional<JTCDto> findByChannelId(String channelId);

}
