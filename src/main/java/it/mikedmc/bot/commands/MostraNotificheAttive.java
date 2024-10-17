package it.mikedmc.bot.commands;

import it.mikedmc.bot.Config;
import it.mikedmc.bot.interfacce.InterfacciaComandi;
import it.mikedmc.bot.model.Anime;
import it.mikedmc.bot.model.Notification;
import it.mikedmc.bot.repository.NotificationRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MostraNotificheAttive implements InterfacciaComandi {

    @Override
    public String getName() {
        return "mostra-notifiche-attive";
    }

    @Override
    public String getDescription() {
        return "Visualizza le notifiche attive, rimuovi gli anime finiti e monitora lo stato degli anime attuali";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {


        int count = NotificationRepository.countByUserId(event.getUser().getId());
        if (count <= 0) {
            event.replyEmbeds(new EmbedBuilder().setDescription("Non hai nessuna notifica attiva, torna quando ne avrai attivate un paio.").build()).setEphemeral(true).queue();
            return;
        }

        List<Button> firstLine = new ArrayList<>();
        firstLine.add(Button.primary("prev~0","Indietro").withDisabled(true));
        if (count > 5)
            firstLine.add(Button.primary("next~5","Avanti"));
        else
            firstLine.add(Button.primary("next~5","Avanti").withDisabled(true));

        firstLine.add(Button.danger("del","Chiudi il menù"));
        firstLine.add(Button.secondary("reload","Aggiorna il menù"));
        firstLine.add(Button.secondary("niente","Notifiche attive totali: " + count).withDisabled(true));


        List<Notification> notifications = NotificationRepository.getAnimesByUserIdWithOffsetAndLimit(event.getUser().getId(),0,5);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Menù Notifiche Attive")
                .setDescription("Questo è il tuo menù per gestire gli anime a cui ti sei segnato/a !\n" +
                        "\n" +
                        "Il primo rigo di bottoni ti permette di muoverti tra i vari anime (se ne hai più di 5), di eliminare questo messaggio e di aggiornare questo messaggio se hai aggiunto anime e non è aggiornato.\nNel secondo rigo di bottoni, premendoli potrai eliminare l'invio di notifiche di un anime.\n" +
                        "_ _\n" +
                        "_ _");
        List<Button> secondLine = new ArrayList<>();

        int counter = 0;
        for (Notification not : notifications) {
            Anime anime = not.getAnime();

            String state = "In Corso";
            if (anime.getEpisodes() == -1) {
                state = "Sconosciuto";
            } else if (anime.getEpisodes() == anime.getEpisode()) {
                state = "Terminato";
            }

            float episode = anime.getEpisode();
            String episodeString = episode+"";
            if (episode%1==0) {
                episodeString = (int) episode + "";
            }

            float episodes = anime.getEpisodes();
            String episodesString = episodes+"";
            if (episodes == -1) {
                episodesString = "Indefinito";
            } else if (episodes%1==0) {
                episodesString = (int) episodes + "";
            }
            embed.addField(new MessageEmbed.Field((++counter) + " | " + anime.getName(),
                    "[**Link Anime**](" + anime.getLinkAnime() + ") - [**Link Immagine**](" + anime.getLinkImage() + ")\n" +
                    "Stato: `" + state + "` - **" + episodeString + "** su **" + episodesString + "**\n" +
                    "Data d'uscita: `" + anime.getReleaseDate() + "`\nNotifiche: " + ((not.isLast())? "`Alla Fine`":"`Sempre`") + "\n" +
                    "_ _\n" +
                    "_ _",false));
            secondLine.add(Button.danger("remove~"+not.getId(),counter+""));
        }
        // errore non hai nessuna notifica salvata.

        event.getUser().openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessageEmbeds(embed.build()).addActionRow(firstLine).addActionRow(secondLine).queue(message -> event.replyEmbeds(new EmbedBuilder().setDescription("Ho aperto il menù nella tua chat privata!\nClicca per vederlo https://discord.com/channels/@me/" + message.getChannel().getId() + "/" + message.getId() ).build()).setEphemeral(true).queue());
        });

    }

}
