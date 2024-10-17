package it.mikedmc.bot.commands;

import it.mikedmc.bot.Config;
import it.mikedmc.bot.interfacce.InterfacciaComandi;
import it.mikedmc.bot.repository.ChannelRepository;
import it.mikedmc.bot.utils.GestoreCanali;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.sql.*;
import java.util.List;

public class DisimpostaCanale implements InterfacciaComandi {

    @Override
    public String getName() {
        return "disimposta-canale";
    }

    @Override
    public String getDescription() {
        return "Animanza smetterà di mandare le sue notifiche";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        // check permessi
        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            // Errore permesso mancante.
            event.replyEmbeds(new EmbedBuilder().setColor(Color.red).setDescription("Errore, non hai il permesso di amministratore per effettuare il comando.").build()).setEphemeral(true).queue();
            return;
        }
        // check tipo canale
        String tipo = event.getChannelType().name();
        if (tipo.equals("TEXT")) {

            // elimino il server dal db, se è presente lo elimina, sennò, ti dice di provare l'altro comando.
            if (ChannelRepository.delete(event.getGuild().getId())) {
                event.replyEmbeds(new EmbedBuilder().setDescription("Ora non manderò più le mie notifiche nel server.").build()).setEphemeral(true).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder().setDescription("In questo canale non stò mandando notifiche.\nProva con /imposta-canale").build()).setEphemeral(true).queue();
            }

        } else {
            // Errore, canale sbagliato.
            event.replyEmbeds(new EmbedBuilder().setColor(Color.red).setDescription("Errore, il tipo del canale dove stai effettuando il comando non và bene. Dubito che io stia mandando le mie notifiche quì.").build()).setEphemeral(true).queue();
        }

    }

}
