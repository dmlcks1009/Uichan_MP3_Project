package kr.or.mrhi.uichanmp3project;


import java.io.Serializable;

public class MusicData implements Serializable {
    private String id;
    private String albumId;
    private String title;
    private String artist;
    private String ok; //좋아요

    public MusicData(String id, String albumId, String title, String artist, String ok) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.artist = artist;
        this.ok = ok;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    @Override
    public String toString() {
        return "MusicData{" +
                "id='" + id +
                ", albumId='" + albumId +
                ", title='" + title  +
                ", artist='" + artist  +
                ", ok='" + ok;
    }
}
