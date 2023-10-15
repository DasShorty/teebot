package de.dasshorty.teebot.api;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

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

    Roles(String roleId) {
        this.roleId = roleId;
    }

    public static boolean hasMemberRole(@NotNull Member member, @NotNull Roles roles) {

        Guild guild = member.getGuild();
        Role roleById = guild.getRoleById(roles.roleId);

        return member.getRoles().contains(roleById);
    }

    public String getRoleId() {
        return this.roleId;
    }
}
