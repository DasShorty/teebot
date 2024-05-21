package de.dasshorty.teebot.tickets;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TicketRepository extends MongoRepository<TicketDto, String> {

    Optional<TicketDto> findByThreadId(String threadId);

}
