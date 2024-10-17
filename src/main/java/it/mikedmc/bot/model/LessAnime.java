package it.mikedmc.bot.model;

public class LessAnime {
    public LessAnime(String name, float episode, float episodes, boolean finished) {
        this.name = name;
        this.episode = episode;
        this.episodes = episodes;
        this.finished = finished;
    }

    private String name;
    private float episode;
    private float episodes;

    private boolean finished;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
