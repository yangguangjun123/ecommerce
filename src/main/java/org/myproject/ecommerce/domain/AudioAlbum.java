package org.myproject.ecommerce.domain;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;
import java.util.List;

public class AudioAlbum extends Product {
    private AudioAlbumDetails details;

    public AudioAlbum() {
    }

    public AudioAlbum(String productId, String sku, String type, String genre, String title, String description,
                      String asin, Shipping shipping, Pricing pricing, int quantity, List<CartedItem> carted,
                      AudioAlbumDetails details ) {
        super(productId, sku, type, genre, title, description, asin, shipping, pricing, quantity, carted);
        this.details = details;
    }

    public AudioAlbumDetails getDetails() {
        return details;
    }

    public void setDetails(AudioAlbumDetails details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AudioAlbum that = (AudioAlbum) o;

        return details != null ? details.equals(that.details) : that.details == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (details != null ? details.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + "{ AudioAlbum{" +
                "details=" + details +
                "} }";
    }

    public static class AudioAlbumDetails {
        @BsonProperty(value = "title")
        private String detailedTitle;
        private String artist;

        @BsonProperty(value = "genre_others")
        private List<String> otherGenres;

        private List<String> tracks;

        @BsonProperty(value = "issue_date")
        private Date issueDate;

        public AudioAlbumDetails() {
        }

        public AudioAlbumDetails(String title, String artist, List<String> otherGenres,
                                 List<String> tracks, Date issueDate) {
            this.detailedTitle = title;
            this.artist = artist;
            this.otherGenres = otherGenres;
            this.tracks = tracks;
            this.issueDate = issueDate;
        }

        public String getDetailedTitle() {
            return detailedTitle;
        }

        public void setDetailedTitle(String detailedTitle) {
            this.detailedTitle = detailedTitle;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public List<String> getOtherGenres() {
            return otherGenres;
        }

        public void setOtherGenres(List<String> otherGenres) {
            this.otherGenres = otherGenres;
        }

        public List<String> getTracks() {
            return tracks;
        }

        public void setTracks(List<String> tracks) {
            this.tracks = tracks;
        }

        public Date getIssueDate() {
            return issueDate;
        }

        public void setIssueDate(Date issueDate) {
            this.issueDate = issueDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AudioAlbumDetails that = (AudioAlbumDetails) o;

            if (detailedTitle != null ? !detailedTitle.equals(that.detailedTitle) : that.detailedTitle != null) return false;
            if (artist != null ? !artist.equals(that.artist) : that.artist != null) return false;
            if (otherGenres != null ? !otherGenres.equals(that.otherGenres) : that.otherGenres != null) return false;
            if (tracks != null ? !tracks.equals(that.tracks) : that.tracks != null) return false;
            return issueDate != null ? issueDate.equals(that.issueDate) : that.issueDate == null;
        }

        @Override
        public int hashCode() {
            int result = detailedTitle != null ? detailedTitle.hashCode() : 0;
            result = 31 * result + (artist != null ? artist.hashCode() : 0);
            result = 31 * result + (otherGenres != null ? otherGenres.hashCode() : 0);
            result = 31 * result + (tracks != null ? tracks.hashCode() : 0);
            result = 31 * result + (issueDate != null ? issueDate.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "AudioAlbumDetails{" +
                    "detailedTitle='" + detailedTitle + '\'' +
                    ", artist='" + artist + '\'' +
                    ", otherGenres=" + otherGenres +
                    ", tracks=" + tracks +
                    ", issueDate=" + issueDate +
                    '}';
        }
    }

    public static class AudioAlbumBuilder extends ProductBuilder {
        private String title;
        private String artist;
        private List<String> otherGenres;
        private List<String> tracks;
        private Date issueDate;

        public AudioAlbumBuilder(String productId, String sku, String type) {
            super(productId, sku, type);
        }

        public AudioAlbumBuilder buildAudioAlbumTitle(String title) {
            this.title = title;
            return this;
        }

        public  AudioAlbumBuilder buildAudioAlbumArtist(String artist) {
            this.artist = artist;
            return this;
        }

        public AudioAlbumBuilder buildAudioAlbumOtherGenres(List<String> otherGenres) {
            this.otherGenres = otherGenres;
            return this;
        }

        public AudioAlbumBuilder buildAudioAlbumTracks(List<String> tracks) {
            this.tracks = tracks;
            return this;
        }

        public AudioAlbumBuilder buildAudioAlbumIssueDate(Date issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public AudioAlbum build() {
            return new AudioAlbum(productId, sku, type, genre, super.title, description, asin,
                    shipping, pricing, quantity, carted , new AudioAlbumDetails(title,
                    artist, otherGenres, tracks, issueDate));
        }
    }
}