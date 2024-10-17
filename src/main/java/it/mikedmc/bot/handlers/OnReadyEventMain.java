package it.mikedmc.bot.handlers;

import it.mikedmc.bot.model.Anime;
import it.mikedmc.bot.model.LessAnime;
import it.mikedmc.bot.repository.AnimeRepository;
import it.mikedmc.bot.repository.ChannelRepository;
import it.mikedmc.bot.repository.NotificationRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class OnReadyEventMain extends ListenerAdapter {

    private Timer timer;

    @Override
    public void onReady(ReadyEvent event) {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Thread(() -> {

                    try {
                        // Connessione al sito web
                        Document document = Jsoup.connect("https://www.animeworld.so").get(); // ha dato un errore.

                        // Seleziona il div desiderato utilizzando un selettore CSS
                        Element div = document.select("div.widget.hotnew.has-page").first().select("div.widget-body").first().select("div.content").first().select("div.content").first();
                        // Seleziona solo i primi 10 div.item
                        List<Element> items = div.select("div.item").subList(0, Math.min(3, div.select("div.item").size()));

                        try {

                            // Itera sui primi 10 elementi e ottieni le informazioni
                            for (Element item : items) {
                                String name = item.select("a.name").text();
                                String imageLink = item.select("a.poster > img").attr("src");
                                String animeLink = "https://www.animeworld.so" + item.select("a.poster").attr("href");
                                float lastEpisode = Float.parseFloat(item.select("div.ep").text().replaceAll("[^0-9.]", ""));


                                // query che controlla se si trova il nome nel database
                                LessAnime lessAnime = AnimeRepository.checkAnime(name);

                                // se cè controlliamo se l'episodio concide con "ultimoEpisodio"
                                if (lessAnime != null) {

                                    float episode = lessAnime.getEpisode();
                                    float episodes = lessAnime.getEpisodes();

                                    // l'anime è terminato
                                    if (episodes == lastEpisode) {
                                        boolean finished = lessAnime.isFinished();
                                        if (!finished) {

                                            // Creazione embed per l'ultimo episodio.
                                            MessageEmbed embed = embedLastEpisode(name, imageLink, animeLink, lastEpisode);

                                            // Mandiamo l'embed a tutti quelli che se lo sono segnati.
                                            NotificationRepository.sentNotificationToAllUsers(name, embed, event);

                                            // Aggiorniamo il record con il numero dell'episodio e se che è finito l'anime.
                                            AnimeRepository.updateAnime(name, lastEpisode, true);

                                            // Eliminiamo gli utenti segnati nelle notifiche dell'anime terminato.
                                            NotificationRepository.deleteNotifications(name);

                                            // Mandiamo l'embed a tutti quelli che si sono segnati al ping dell'anime finito


                                            return;
                                        }
                                        return;
                                    }

                                    try {

                                        // se non cooncide, facciamo una query e prendiamo tutti quelli che si son aggiunti quell'anime, e gli inviamo il messaggio in privato.
                                        if (lastEpisode > episode) {
                                            // aggiorniamo la puntata e mandiamo a tutti quelli che hanno salvato l'anime, l'anime.
                                            MessageEmbed embed = embedEpisode(name, imageLink, animeLink, lastEpisode);
                                            NotificationRepository.sentNotificationToUsers(name, embed, event);
                                            AnimeRepository.updateAnime(name, lastEpisode, false);
                                            return;
                                        }
                                    } catch (Exception e) {

                                    }

                                } else {

                                    animeLink = animeLink.substring(0, animeLink.length() - 7);
                                    Document doc = Jsoup.connect(animeLink).get();
                                    Element d = doc.select("div.widget-body > div.row > div.info > div.row").first();
                                    List<Element> dati = d.select("dl.meta > dd");

                                    String description = doc.select("div.widget-body > div.row > div.info > div.desc").first().text();
                                    // Estrazione delle informazioni
                                    String releaseDate = dati.get(2).text();  // 05 Aprile 2024
                                    LocalDate releaseDateFormatted = convertDate(releaseDate);
                                    String studio = dati.get(4).select("a").get(0).text();  // SynergySP
                                    String genre = dati.get(5).text();      // Commedia, Romantico, Sci-Fi, Sentimentale, Slice of Life, Soprannaturale

                                    float episodes = -1;
                                    if (!dati.get(8).text().equals("??")) {
                                        episodes = Float.parseFloat(dati.get(8).text());
                                    }

                                    String malLink = extractLink(doc, "a.mal");
                                    String aniListLink = extractLink(doc, "a.anilist");
                                    String mangaWorldLink = extractLink(doc, "a.mangaworld");


                                    Anime anime = new Anime(name, imageLink, animeLink, episodes, description, releaseDate, releaseDateFormatted, studio, genre, malLink, aniListLink, mangaWorldLink);

                                    if (AnimeRepository.save(anime)) {
                                        AnimeRepository.sendToChannels(anime, ChannelRepository.findAll(event));
                                    }

                                }
                            }
                        } catch (NumberFormatException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }).start();
            }
        }, 0, (60 * 30) * 1000); // Esegue il task ogni 60 secondi (60 * 1000 millisecondi)

        // loop infinito (poichè lo dovrà fare all'ìnfinito ogni 30 minuti (5 nel testing) dovrà fare la chiamata api

    }

    private static String extractLink(Document doc, String cssQuery) {
        Element linkElement = doc.select(cssQuery).first();
        return linkElement != null ? linkElement.attr("href") : null;
    }

    private MessageEmbed embedEpisode (String nome, String linkImmagine, String linkAnime, float episodio) {

        String episodeString = episodio+"";
        if (episodio == -1) {
            episodeString = "Indefinito";
        } else if (episodio%1==0) {
            episodeString = (int) episodio + "";
        }
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(nome,linkAnime)
                .setDescription("**Episodio** : `" + episodeString + "`\n")
                .setImage(linkImmagine)
                .setFooter("ᴘᴏᴡᴇʀᴇᴅ ʙʏ ᴍɪᴋᴇᴅᴍᴄ","https://cdn.discordapp.com/avatars/183542841732104192/94fa2892db53fb5ec0ee2a8f36b39582?size=1024");
        return embed.build();
    }

    private MessageEmbed embedLastEpisode (String nome, String linkImmagine, String linkAnime, float episodio) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(nome,linkAnime)
                .setDescription("**Episodio** : `" + episodio + "`\n**L'anime è terminato.**\n" +
                        "```È stato rimosso dalle \n" +
                        "tue notifiche attive.```")
                .setImage(linkImmagine)
                .setFooter("ᴘᴏᴡᴇʀᴇᴅ ʙʏ ᴍɪᴋᴇᴅᴍᴄ","https://cdn.discordapp.com/avatars/183542841732104192/94fa2892db53fb5ec0ee2a8f36b39582?size=1024");
        return embed.build();
    }

    public static LocalDate convertDate(String date) {
        String[] parts = date.split(" ");
        int day = Integer.parseInt(parts[0]);
        String month = parts[1].toLowerCase(); // Lo converto in minuscolo per uniformità
        int year = Integer.parseInt(parts[2]);

        int monthNumber;

        switch (month) {
            case "gennaio":
                monthNumber = 1;
                break;
            case "febbraio":
                monthNumber = 2;
                break;
            case "marzo":
                monthNumber = 3;
                break;
            case "aprile":
                monthNumber = 4;
                break;
            case "maggio":
                monthNumber = 5;
                break;
            case "giugno":
                monthNumber = 6;
                break;
            case "luglio":
                monthNumber = 7;
                break;
            case "agosto":
                monthNumber = 8;
                break;
            case "settembre":
                monthNumber = 9;
                break;
            case "ottobre":
                monthNumber = 10;
                break;
            case "novembre":
                monthNumber = 11;
                break;
            case "dicembre":
                monthNumber = 12;
                break;
            default:
                throw new IllegalArgumentException("Mese non valido: " + month);
        }

        // Ritorno un oggetto LocalDate con il giorno, mese e anno
        return LocalDate.of(year, monthNumber, day);
    }

}