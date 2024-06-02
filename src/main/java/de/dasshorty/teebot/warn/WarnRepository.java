package de.dasshorty.teebot.warn;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface WarnRepository extends MongoRepository<UserWarnDto, String> {
}
