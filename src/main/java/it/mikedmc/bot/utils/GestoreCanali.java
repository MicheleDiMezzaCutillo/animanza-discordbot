package it.mikedmc.bot.utils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GestoreCanali {
    public Set<TextChannel> canaliTestuali;
    private Set<VoiceChannel> canaliVocali;
    private Set<NewsChannel> canaliNews;

    public Set<TextChannel> getCanali() {
        return canaliTestuali;
    }

    public void addCanale (TextChannel channel) {
        canaliTestuali.add(channel);
    }
    public void addCanale (VoiceChannel channel) {
        canaliVocali.add(channel);
    }
    public void addCanale(NewsChannel channel) {
        canaliNews.add(channel);
    }

    public void removeCanale (TextChannel channel) {
        canaliTestuali.remove(channel);
    }

    public void removeCanale (VoiceChannel channel) {
        canaliVocali.remove(channel);
    }
    public void removeCanale(NewsChannel channel) {
        canaliNews.remove(channel);
    }

    public void setCanaliTestuali(Set<TextChannel> canali) {
        this.canaliTestuali = canali;
    }
    public void setCanaliVocali(Set<VoiceChannel> canali) {
        this.canaliVocali = canali;
    }
    public void setCanaliNews(Set<NewsChannel> canali) {
        this.canaliNews = canali;
    }

    public void notificaCanali (MessageEmbed embed, ActionRow bottoni, boolean bottoniVisibili, boolean pingUltimoEpisodio) {

        if (bottoniVisibili) {
            List<Button> bottoniBase = new ArrayList<>();
            bottoniBase.add(Button.success("add","Tienimi aggiornato"));
            bottoniBase.add(Button.danger("rep","Report"));
            if (pingUltimoEpisodio) {
                bottoniBase.add(Button.primary("last","Avvisami quando finisce"));
            }

            if (canaliTestuali!=null){
                for (TextChannel channel : canaliTestuali)
                    channel.sendMessageEmbeds(embed).setComponents(bottoni).addActionRow(bottoniBase).queue();


            }

            if (canaliVocali!=null) {
                for (VoiceChannel channel : canaliVocali)
                    channel.sendMessageEmbeds(embed).setComponents(bottoni).addActionRow(bottoniBase).queue();
            }

            if (canaliNews!=null) {
                for (NewsChannel channel : canaliNews)
                    channel.sendMessageEmbeds(embed).setComponents(bottoni).addActionRow(bottoniBase).queue();
            }
        } else {
            if (canaliTestuali!=null){
                for (TextChannel channel : canaliTestuali)
                    channel.sendMessageEmbeds(embed).setComponents(bottoni).queue();
            }

            if (canaliVocali!=null) {
                for (VoiceChannel channel : canaliVocali)
                    channel.sendMessageEmbeds(embed).setComponents(bottoni).queue();
            }

            if (canaliNews!=null) {
                for (NewsChannel channel : canaliNews)
                    channel.sendMessageEmbeds(embed).setComponents(bottoni).queue();
            }
        }

    }

    public void notificaCanali (MessageEmbed embed) {

            if (canaliTestuali!=null){
                for (TextChannel channel : canaliTestuali)
                    channel.sendMessageEmbeds(embed).queue();
            }

            if (canaliVocali!=null) {
                for (VoiceChannel channel : canaliVocali)
                    channel.sendMessageEmbeds(embed).queue();
            }

            if (canaliNews!=null) {
                for (NewsChannel channel : canaliNews)
                    channel.sendMessageEmbeds(embed).queue();
            }

    }
}
