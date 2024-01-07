package de.dasshorty.teebot.api;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public enum Roles {

    ADMIN("835975576983830570"),
    DEVELOPER("867837552479698992"),
    STAFF("862095492236967947"),
    ARTIST("1183539470692270111"),
    ON_AIR("835997054676500570"),
    CREATOR("835997013660139590"),
    BOOSTER("853413781464023070"),
    FAMILIY("1193330376001720370"),
    FRIENDS("835976173531693096"),
    SUBSCRIBER("1096108561366523914");

    private final String roleId;

    Roles(String roleId) {
        this.roleId = roleId;
    }

    public static boolean hasMemberRole(@NotNull Member member, @NotNull Roles... roles) {

        Guild guild = member.getGuild();

        AtomicBoolean bool = new AtomicBoolean(false);

        for (Roles role : roles) {

            Role roleById = guild.getRoleById(role.roleId);
            if (member.getRoles().contains(roleById))
                bool.set(true);


        }


        return bool.get();
    }

    public String getRoleId() {
        return this.roleId;
    }
}
