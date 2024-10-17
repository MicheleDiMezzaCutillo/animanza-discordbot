package it.mikedmc.bot.handlers;

import it.mikedmc.bot.model.Anime;
import it.mikedmc.bot.model.Notification;
import it.mikedmc.bot.repository.AnimeRepository;
import it.mikedmc.bot.repository.NotificationRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OnButtonInteractionEventMostraNotificheAttive extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();

        if (buttonId.equals("add")) {
            String name = event.getMessage().getEmbeds().get(0).getTitle();
            // query per aggiungere persona e nome al database. se già c'è, errore?
            User user = event.getUser();
            if (NotificationRepository.isAlreadyRegistered(user.getId(),name)) {
                event.replyEmbeds(new EmbedBuilder()
                        .setColor(Color.red)
                        .setDescription("Errore, già sei registrato/a a quest'anime.")
                        .build()).setEphemeral(true).queue();
                return;
            }
            NotificationRepository.save(user.getId(),name,false);
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("# Registrato con successo.\n**Nome** : `" + name + "`\nTi manderò un messaggio all'uscita di ogni nuova puntata.")
                    .setColor(Color.GREEN)
                    .build()).setEphemeral(true).queue();
        }

        if (buttonId.equals("rep")) {
            event.replyEmbeds(new EmbedBuilder().setDescription("Ti pare che si possa reportare.. Il bugdet ha un limite.").setColor(Color.red).build()).setEphemeral(true).queue();
        }

        if (buttonId.equals("last")) { // todo aggiungere questa logica
            String name = event.getMessage().getEmbeds().get(0).getTitle();
            // query per aggiungere persona e nome al database. se già c'è, errore?
            User user = event.getUser();
            if (NotificationRepository.isAlreadyRegistered(user.getId(),name)) {
                event.replyEmbeds(new EmbedBuilder()
                        .setColor(Color.red)
                        .setDescription("Errore, già sei registrato/a a quest'anime.")
                        .build()).setEphemeral(true).queue();
                return;
            }
            NotificationRepository.save(user.getId(),name,true);
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("# Registrato con successo.\n**Nome** : `" + name + "`\nTi manderò un messaggio all'uscita dell'ultima puntata.")
                    .setColor(Color.GREEN)
                    .build()).setEphemeral(true).queue();        }

        if (buttonId.equals("del")) {
            event.getMessage().delete().queue();
        }

        if (buttonId.startsWith("next~")) {
            int count = NotificationRepository.countByUserId(event.getUser().getId());
            if (count <= 0) {
                reload(event);
                return;
            }
            int index = Integer.parseInt(buttonId.substring(5));


            List<Button> firstLine = new ArrayList<>();
            firstLine.add(Button.primary("prev~" + index,"Indietro"));
            if (count > index+5) {
                firstLine.add(Button.primary("next~" + (index+5),"Avanti"));
            } else {
                firstLine.add(Button.primary("next~" + (index+5),"Avanti").withDisabled(true));
            }
            firstLine.add(Button.danger("del","Chiudi il menù"));
            firstLine.add(Button.secondary("reload","Aggiorna il menù"));
            firstLine.add(Button.secondary("niente","Notifiche attive totali: " + count).withDisabled(true));

            List<Notification> notifications = NotificationRepository.getAnimesByUserIdWithOffsetAndLimit(event.getUser().getId(),index,index+5);
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

            List<ActionRow> components = new ArrayList<>();
            components.add(ActionRow.of(firstLine));
            components.add(ActionRow.of(secondLine));
            event.editMessageEmbeds(embed.build()).setComponents(components).queue();
            return;
        }

        if (buttonId.startsWith("prev~")) {
            int count = NotificationRepository.countByUserId(event.getUser().getId());
            int index = Integer.parseInt(buttonId.substring(5));
            List<Button> firstLine = new ArrayList<>();

            if (index-5 == 0) {
                firstLine.add(Button.primary("prev~0","Indietro").withDisabled(true));
            } else {
                firstLine.add(Button.primary("prev~" + (index-5),"Indietro"));

            }
            firstLine.add(Button.primary("next~" + index,"Avanti"));
            firstLine.add(Button.danger("del","Chiudi il menù"));
            firstLine.add(Button.secondary("reload","Aggiorna il menù"));
            firstLine.add(Button.secondary("niente","Notifiche attive totali: " + count).withDisabled(true));

            List<Notification> notifications = NotificationRepository.getAnimesByUserIdWithOffsetAndLimit(event.getUser().getId(),index-5,index);
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

            List<ActionRow> components = new ArrayList<>();
            components.add(ActionRow.of(firstLine));
            components.add(ActionRow.of(secondLine));
            event.editMessageEmbeds(embed.build()).setComponents(components).queue();
            return;
        }

        if (buttonId.equals("reload")) {
            // metodo riutilizzabile ;)
            reload(event);
        }

        if (buttonId.startsWith("send~")) {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                // Errore permesso mancante.
                event.replyEmbeds(new EmbedBuilder().setColor(Color.red).setDescription("Errore, non hai il permesso di amministratore per usare questo bottone.").build()).setEphemeral(true).queue();
                return;
            }

            int index = Integer.parseInt(buttonId.substring(5));
            TextChannel channel = event.getChannel().asTextChannel();
            try {
                for (Anime anime : AnimeRepository.findAnimesInProgressLimited(index)) {
                        AnimeRepository.sendToChannel(anime,channel);

                }
                event.editMessageEmbeds(new EmbedBuilder().setDescription("Ti ho inviato gli anime nel canale, se hai premuto un numero, e sono stati inviati meno anime del previsto non preoccuparti, semplicemente non ci sono così tanti anime in uscita al momento.\nPremi il bottone rosso per eliminare questo messaggio, e goditi gli anime che stanno uscendo e che usciranno.").build()).setActionRow(Button.danger("del","Rimuovi questo messaggio.")).queue();
            } catch (InsufficientPermissionException e) {
                event.reply("Purtroppo senza il permesso **VIEW_CHANNEL** non posso mandare le mie notifiche quì.").setEphemeral(true).queue();
            }
        }

        if (buttonId.startsWith("remove~")) {
            int index = (int) Float.parseFloat(buttonId.substring(7));
            NotificationRepository.deleteById(index);
            reload(event);
        }
    }

    public void reload (ButtonInteractionEvent event) {
        int count = NotificationRepository.countByUserId(event.getUser().getId());
        if (count <= 0) {
            event.replyEmbeds(new EmbedBuilder().setDescription("Non hai più nessuna notifica attiva, torna quando ne avrai attivate altre.").build()).setEphemeral(true).queue();
            event.getMessage().delete().queue();
            return;
        }

        List<net.dv8tion.jda.api.interactions.components.buttons.Button> firstLine = new ArrayList<>();
        firstLine.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("prev~0","Indietro").withDisabled(true));
        if (count > 5)
            firstLine.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("next~5","Avanti"));
        else
            firstLine.add(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("next~5","Avanti").withDisabled(true));

        firstLine.add(net.dv8tion.jda.api.interactions.components.buttons.Button.danger("del","Chiudi il menù"));
        firstLine.add(net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("reload","Aggiorna il menù"));
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

        List<ActionRow> components = new ArrayList<>();
        components.add(ActionRow.of(firstLine));
        components.add(ActionRow.of(secondLine));
        event.editMessageEmbeds(embed.build()).setComponents(components).queue();
    }
}
