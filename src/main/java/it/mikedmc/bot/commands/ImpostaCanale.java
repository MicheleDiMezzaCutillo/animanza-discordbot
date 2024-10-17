package it.mikedmc.bot.commands;

import it.mikedmc.bot.Config;
import it.mikedmc.bot.interfacce.InterfacciaComandi;
import it.mikedmc.bot.model.Anime;
import it.mikedmc.bot.repository.AnimeRepository;
import it.mikedmc.bot.repository.ChannelRepository;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
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
import java.util.concurrent.TimeUnit;

public class ImpostaCanale implements InterfacciaComandi {

    @Override
    public String getName() {
        return "imposta-canale";
    }

    @Override
    public String getDescription() {
        return "Imposta il canale testuale per le notifiche di Animanza";
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
        // Controlliamo che sia un canale testuale
        if (tipo.equals("TEXT")) {

            Guild guild = event.getGuild();
            Channel channel = event.getChannel();

            try {
                event.getChannel().sendMessage("Controllo se ho i permessi per scrivere quì, ora tolgo subito il messaggio.").queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
            } catch (InsufficientPermissionException e) {
                event.reply("Purtroppo senza il permesso **VIEW_CHANNEL** non posso  mandare le mie notifiche quì, quindi, non posso impostare il canale.").setEphemeral(true).queue();
                return;
            }

            if (ChannelRepository.save(guild.getId(),channel.getId(),guild.getName(),channel.getName())) {
                event.replyEmbeds(new EmbedBuilder().setDescription("Da adesso, invierò in questo canale una notifica all'uscita di un nuovo anime con tutti i dettagli!\nPerò il canale ora come ora sembra un po' vuoto..\nChe ne dici di premere uno dei bottoni sotto per mandare già qualche notifica? Puoi scegliere tra **Nessuno** - **5** - **10** - **15** - **Tutti quelli in corso**").build()).setActionRow(
                        Button.danger("del","Nessuno"),
                        Button.primary("send~5","5 Anime"),
                        Button.primary("send~10","10 Anime"),
                        Button.primary("send~15","15 Anime"),
                        Button.success("send~-1","Tutti")
                ).queue();

            } else {
                event.replyEmbeds(new EmbedBuilder().setDescription("Purtroppo non sono riuscita ad aggiungere questo canale nella mia lista.").build()).setEphemeral(true).queue();
            }
        } else {
            // Errore, canale sbagliato.
            event.replyEmbeds(new EmbedBuilder().setColor(Color.red).setDescription("Errore, il tipo del canale dove stai effettuando il comando non và bene, prova un canale testuale.").build()).setEphemeral(true).queue();
        }
    }
}
