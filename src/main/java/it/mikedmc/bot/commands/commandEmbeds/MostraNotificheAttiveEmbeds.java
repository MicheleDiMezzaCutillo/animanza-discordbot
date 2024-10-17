package it.mikedmc.bot.commands.commandEmbeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class MostraNotificheAttiveEmbeds {


    public static MessageEmbed NonHaiAltriAnimeSegnati () {
        return new EmbedBuilder()
                .setDescription(":x: Non hai altri anime segnati da mostrarti.")
                .setColor(Color.RED)
                .build();
    }
}
