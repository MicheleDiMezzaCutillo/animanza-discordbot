package it.mikedmc.bot.commands;

import it.mikedmc.bot.Config;
import it.mikedmc.bot.interfacce.InterfacciaComandi;
import it.mikedmc.bot.repository.ChannelRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.w3c.dom.Text;

import java.sql.*;
import java.util.List;

public class Info implements InterfacciaComandi {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Breve spiegazione su come funziona Animanza";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        // controllo se nel server è stato impostato un canale.
        TextChannel channel = ChannelRepository.findByGuildId(event, event.getGuild().getId());
        String canale = "";
        // se non è stato impostato, gli dico "usa il comando"
        if (channel==null) {
            canale = "\nche imposti con il comando: </imposta-canale:1241577760011587615>";

            // sennò gli dico " il canale è questo ".
        } else {
            canale = channel.getAsMention();
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setDescription(
                        "## Chi sono?\n" +
                        "Sono stata progettata da <@183542841732104192>.\n" +
                        "In base ai fondi e le idee, potrei aggiornarmi in futuro.\n" +
                        "\n" +
                        "## A che servo?\n" +
                        "Quando esce un nuovo anime, \nmanderò una notifica nel canale " + canale + "." +
                        "\nSotto la notifica ci troverai dei bottoni:\n" +
                        "- **Anime World** : Vai a guardare l'anime.\n" +
                        "- **My Anime List** : Aggiungilo anche su MAL.\n" +
                        "- **AniList** : Scopri i dettagli dell'anime.\n" +
                        "- **Manga World** : Vai a leggere il Manga.\n" +
                        "Mentre nella riga sotto troverai:\n" +
                        "- **Tienimi Aggiornato** : All'uscita di un nuovo episodio ti arriverà un messaggio privato da me.\n" +
                        "- **Report** :  Ma che vuoi reportare..\n" +
                        "- **Avvisami quando finisce** :  Quando esce l'ultimo episodio dell'anime ti avvertirò.\n" +
                        "\n" +
                        "Nel caso non vediate un bottone è poichè alcuni anime non hanno Manga o ancora non si sà quante puntate avranno, e quindi è impossibile per me avvisarvi che l'anime è finito se neanche io sò quando finirà.\n" +
                        "\n" +
                        "## Comandi\n" +
                        "- </imposta-canale:1241577760011587615> - Serve ad impostare un canale dove manderò le notifiche degli anime che escono. Solo per gli amministratori.\n" +
                        "- </disimposta-canale:1257970495027155045> - Serve a rimuovere il canale dove mandavo le notifiche degli anime. Solo per gli amministratori.\n" +
                        "- </mostra-notifiche-attive:1241755903632609310> - Ti mostro la lista di anime a cui ti sei segnato/a.\n" +
                        "- </info:1257970497459585105> - È questo comando che hai utilizzato.\n" +
                        "\n" +
                        "## Altro\n" +
                        "Se hai qualche idea che potrei aggiungere, contattami!\n" +
                        "\n" +
                        "## Publicità\n" +
                        "<:8454verified:1207201329320304672>  Hai un idea per un bot che vorresti realizzare?\n" +
                        "<:4928applicationbot:1207201318079299604> Contattami e creerò il tuo bot discord personalizzato.\n" +
                        "<:9961developer:1207201339826769951> Con oltre 20 bot creati compreso questo"
                );


        event.replyEmbeds(embed.build())
                .setActionRow(
                        Button.link("https://mikedmc.it","Sito Web"),
                        Button.link("https://discord.gg/8e5PYG9NFa","Server Discord"),
                        Button.link("https://discord.com/oauth2/authorize?client_id=1241100527505375362","Aggiungi Animanza ad un Server")
                ).setEphemeral(true).queue();
    }

}
