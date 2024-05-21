package de.dasshorty.teebot.embedcreator;

import de.dasshorty.teebot.embedcreator.dto.EmbedDto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EmbedRepository extends MongoRepository<EmbedDto, String> {

    Optional<EmbedDto> getEmbedDtoByEmbedId(String embedId);

    List<EmbedDto> getEmbedDtosByEmbedDescriptionContaining(String query);

}
