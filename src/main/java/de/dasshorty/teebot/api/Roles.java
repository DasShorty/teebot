package de.dasshorty.teebot.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public enum Roles {

    ADMIN("1159074658168090726"),
    DEVELOPER("1159086761763414046"),
    SUPPORTER("1159074698013982720"),
    MODERATOR("1159074718104686692"),
    HEAD_MODERATOR("1159074740590346321"),
    DONATOR("1159074764204298241"),
    FRIENDS("1159075563701547049"),
    CREATOR("1159075603434192926"),
    PREMIUM("1159075677715304458"),
    MEMBER("1159086959327715380");


    private final String roleId;

    public static boolean hasMemberRole(@NotNull Member member, @NotNull Roles roles) {

        val guild = member.getGuild();
        val roleById = guild.getRoleById(roles.roleId);

        return member.getRoles().contains(roleById);
    }
}
