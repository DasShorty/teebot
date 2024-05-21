package de.dasshorty.teebot.tickets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {

    @Id
    private String ticketId;
    private String openerId = null;
    private String threadId = null;
    private TicketReason reason = null;
    private String description = null;
    private List<TicketMessageData> messages = new ArrayList<>();
}
