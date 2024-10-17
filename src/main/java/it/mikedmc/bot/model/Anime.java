package it.mikedmc.bot.model;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Anime {

    public Anime () {}

    public Anime (String name, String linkImage, String linkAnime, float episode) {
        this.name = name;
        this.linkImage = linkImage;
        this.linkAnime = linkAnime;
        this.episode = episode;
    }

    public Anime(String name, String linkImage, String linkAnime, float episodes, String description, String releaseDate, LocalDate releaseDateFormatted, String studio, String genres, String malLink, String aniListLink, String mangaWorldLink) {
        this.name = name;
        this.linkImage = linkImage;
        this.linkAnime = linkAnime;
        this.episode = 1;
        this.episodes = episodes;
        this.description = description;
        this.releaseDate = releaseDate;
        this.releaseDateFormatted = releaseDateFormatted;
        this.studio = studio;
        this.genres = genres;
        this.malLink = malLink;
        this.aniListLink = aniListLink;
        this.mangaWorldLink = mangaWorldLink;
    }

    public Anime(String name, String linkImage, String linkAnime, float episode, float episodes, String description, String releaseDate, LocalDate releaseDateFormatted, String studio, String genres, String malLink, String aniListLink, String mangaWorldLink) {
        this.name = name;
        this.linkImage = linkImage;
        this.linkAnime = linkAnime;
        this.episode = episode;
        this.episodes = episodes;
        this.description = description;
        this.releaseDate = releaseDate;
        this.releaseDateFormatted = releaseDateFormatted;
        this.studio = studio;
        this.genres = genres;
        this.malLink = malLink;
        this.aniListLink = aniListLink;
        this.mangaWorldLink = mangaWorldLink;
    }

    private String name; // Id
    private String linkImage;
    private String linkAnime;
    private float episode;
    private float episodes;
    private boolean finished;
    private String description;
    private String releaseDate;
    private LocalDate releaseDateFormatted;
    private String studio;
    private String genres;
    private String malLink;
    private String aniListLink;
    private String mangaWorldLink;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public String getLinkAnime() {
        return linkAnime;
    }

    public void setLinkAnime(String linkAnime) {
        this.linkAnime = linkAnime;
    }

    public float getEpisode() {
        return episode;
    }

    public void setEpisode(float episode) {
        this.episode = episode;
    }

    public float getEpisodes() {
        return episodes;
    }

    public void setEpisodes(float episodes) {
        this.episodes = episodes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public LocalDate getReleaseDateFormatted() {
        return releaseDateFormatted;
    }

    public void setReleaseDateFormatted(LocalDate releaseDateFormatted) {
        this.releaseDateFormatted = releaseDateFormatted;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getMangaWorldLink() {
        return mangaWorldLink;
    }

    public void setMangaWorldLink(String mangaWorldLink) {
        this.mangaWorldLink = mangaWorldLink;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getMalLink() {
        return malLink;
    }

    public void setMalLink(String malLink) {
        this.malLink = malLink;
    }

    public String getAniListLink() {
        return aniListLink;
    }

    public void setAniListLink(String aniListLink) {
        this.aniListLink = aniListLink;
    }

    public ActionRow getBottoniExtra () {
        List<Button> bottoni = new ArrayList<>();
        bottoni.add(Button.link(linkAnime,"Anime World"));
        // Estrazione dei link
        if (malLink != null) {
            bottoni.add(
                    Button.link(malLink,"My Anime List")
            );
        }
        if (aniListLink != null) {
            bottoni.add(
                    Button.link(aniListLink,"Ani List")
            );
        }
        if (mangaWorldLink != null) {
            bottoni.add(
                    Button.link(mangaWorldLink,"Manga World")
            );
        }
        return ActionRow.of(bottoni);
    }
}
