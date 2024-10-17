package it.mikedmc.bot.model;

public class Notification {
    private float id;
    private Anime anime; // foreign key con Anime.
    private String user_id;

    private boolean last;

    public float getId() {
        return id;
    }

    public void setId(float id) {
        this.id = id;
    }

    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
