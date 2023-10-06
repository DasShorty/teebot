package de.dasshorty.teebot.selfroles;

import de.dasshorty.teebot.api.menu.string.StringSelectionMenu;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

@RequiredArgsConstructor
public class SelfRoleMenu implements StringSelectionMenu {

    private final SelfRoleDatabase selfRoleDatabase;

    @Override
    public String id() {
        return "self-roles";
    }

    @Override
    public void onExecute(@NotNull StringSelectInteractionEvent event) {

        val member = event.getMember();
        assert member != null;

        event.deferReply(true).queue();

        event.getSelectedOptions().forEach(selectOption -> {

            val roleId = selectOption.getValue();

            val roleById = Objects.requireNonNull(event.getGuild()).getRoleById(roleId);

            if (OnlyRole.getColorRoleIds().contains(roleId))
                OnlyRole.getColorRoleIds().forEach(colorRoleId -> event.getGuild().removeRoleFromMember(member, Objects.requireNonNull(event.getGuild().getRoleById(colorRoleId))).queue());

            assert roleById != null;
            event.getGuild().addRoleToMember(member, roleById).queue();
        });

        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                .setAuthor("Selfrole")
                .setDescription("Wir haben für dich die Rollen angepasst.")
                .setColor(Color.GREEN)
                .build()).queue();

    }
}
