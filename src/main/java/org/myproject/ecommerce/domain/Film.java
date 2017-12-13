package org.myproject.ecommerce.domain;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;
import java.util.List;

public class Film extends Product{
    private FilmDetails details;

    public Film() {
    }

    public Film(String productId, String sku, String department, String type, String genre,
                String title, String description, String asin, Shipping shipping, Pricing pricing,
                int quantity, List<CartedItem> carted, FilmDetails details ) {
        super(productId, sku, department, type, genre, title, description, asin, shipping, pricing, quantity, carted);
        this.details = details;
    }

    public FilmDetails getDetails() {
        return details;
    }

    public void setDetails(FilmDetails details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Film film = (Film) o;

        return details != null ? details.equals(film.details) : film.details == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (details != null ? details.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + "{ Film{" +
                "details=" + details +
                "} }";
    }

    public static class FilmDetails {
        private String title;
        private List<String> director;
        private List<String> writer;

        @BsonProperty(value = "aspect_ratio")
        private String aspectRatio;

        @BsonProperty(value = "issue_date")
        private Date issueDate;

        @BsonProperty(value = "genre_others")
        private List<String> otherGenres;

        private String actor;

        public FilmDetails() {
        }

        public FilmDetails(String title, List<String> director, List<String> writer, String aspectRatio,
                           Date issueDate, List<String> otherGenres, String actor) {
            this.title = title;
            this.director = director;
            this.writer = writer;
            this.aspectRatio = aspectRatio;
            this.issueDate = issueDate;
            this.otherGenres = otherGenres;
            this.actor = actor;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getDirector() {
            return director;
        }

        public void setDirector(List<String> director) {
            this.director = director;
        }

        public List<String> getWriter() {
            return writer;
        }

        public void setWriter(List<String> writer) {
            this.writer = writer;
        }

        public String getAspectRatio() {
            return aspectRatio;
        }

        public void setAspectRatio(String aspectRatio) {
            this.aspectRatio = aspectRatio;
        }

        public Date getIssueDate() {
            return issueDate;
        }

        public void setIssueDate(Date issueDate) {
            this.issueDate = issueDate;
        }

        public List<String> getOtherGenres() {
            return otherGenres;
        }

        public void setOtherGenres(List<String> otherGenres) {
            this.otherGenres = otherGenres;
        }

        public String getActor() {
            return actor;
        }

        public void setActor(String actor) {
            this.actor = actor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FilmDetails that = (FilmDetails) o;

            if (title != null ? !title.equals(that.title) : that.title != null) return false;
            if (director != null ? !director.equals(that.director) : that.director != null) return false;
            if (writer != null ? !writer.equals(that.writer) : that.writer != null) return false;
            if (aspectRatio != null ? !aspectRatio.equals(that.aspectRatio) : that.aspectRatio != null) return false;
            if (issueDate != null ? !issueDate.equals(that.issueDate) : that.issueDate != null) return false;
            if (otherGenres != null ? !otherGenres.equals(that.otherGenres) : that.otherGenres != null) return false;
            return actor != null ? actor.equals(that.actor) : that.actor == null;
        }

        @Override
        public int hashCode() {
            int result = title != null ? title.hashCode() : 0;
            result = 31 * result + (director != null ? director.hashCode() : 0);
            result = 31 * result + (writer != null ? writer.hashCode() : 0);
            result = 31 * result + (aspectRatio != null ? aspectRatio.hashCode() : 0);
            result = 31 * result + (issueDate != null ? issueDate.hashCode() : 0);
            result = 31 * result + (otherGenres != null ? otherGenres.hashCode() : 0);
            result = 31 * result + (actor != null ? actor.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "FilmDetails{" +
                    "title='" + title + '\'' +
                    ", director=" + director +
                    ", writer=" + writer +
                    ", aspectRatio='" + aspectRatio + '\'' +
                    ", issueDate=" + issueDate +
                    ", otherGenres=" + otherGenres +
                    ", actor='" + actor + '\'' +
                    '}';
        }
    }

    public static class FilmBuilder extends ProductBuilder {
        private String title;
        private List<String> director;
        private List<String> writer;
        private String aspectRatio;
        private Date issueDate;
        private List<String> otherGenres;
        private String actor;

        public FilmBuilder(String productId, String sku, String type) {
            super(productId, sku, type);
        }

        public Film.FilmBuilder buildFilmTitle(String title) {
            this.title = title;
            return this;
        }

        public Film.FilmBuilder buildFilmDirector(List<String> director) {
            this.director = director;
            return this;
        }

        public Film.FilmBuilder buildFilmWriter(List<String> writer) {
            this.writer = writer;
            return this;
        }

        public Film.FilmBuilder buildFilmOtherGenres(List<String> otherGenres) {
            this.otherGenres = otherGenres;
            return this;
        }

        public Film.FilmBuilder buildAspectRatio(String aspectRatio) {
            this.aspectRatio = aspectRatio;
            return this;
        }

        public Film.FilmBuilder buildFilmIssueDate(Date issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public Film.FilmBuilder buildActor(String actor) {
            this.actor = actor;
            return this;
        }

        public Film build() {
            return new Film(productId, sku, department, type, genre, super.title, description, asin,
                    shipping, pricing, quantity, carted, new Film.FilmDetails(title,
                    director, writer, aspectRatio, issueDate, otherGenres, actor));
        }
    }
}
