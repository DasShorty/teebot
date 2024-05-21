package de.dasshorty.teebot.giveaways;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GiveawayRepository extends MongoRepository<GiveawayDto, String> {

    List<GiveawayDto> getAllByActiveIsTrue();

}
