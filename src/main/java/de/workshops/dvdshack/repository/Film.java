package de.workshops.dvdshack.repository;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Film extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILM_ID_SEQ")
    @SequenceGenerator(name = "FILM_ID_SEQ", sequenceName = "film_film_id_seq", allocationSize = 1)
    @Column(name = "film_id", nullable = false)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Convert(converter = YearConverter.class)
    @Column(columnDefinition = "year")
    private Year releaseYear;

    @ManyToOne(optional = false)
    @JoinColumn(name = "language_id")
    private Language language;

    @ManyToOne
    @JoinColumn(name = "original_language_id")
    private Language originalLanguage;

    @Column(nullable = false)
    private short rentalDuration;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal rentalRate;

    private short length;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal replacementCost;

    @Enumerated(EnumType.STRING)
    @Type(value = RatingType.class)
    @Column(columnDefinition = "mpaa_rating")
    private Rating rating;

    @Type(ListArrayType.class)
    @Column(name = "special_features", columnDefinition = "text[]")
    private List<String> specialFeatures;

    @ManyToMany
    @JoinTable(
            name = "film_actor",
            joinColumns = @JoinColumn(
                    name = "film_id", referencedColumnName = "film_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "actor_id", referencedColumnName = "actor_id"
            )
    )
    Set<Actor> actors;

    @ManyToMany
    @JoinTable(
            name = "film_category",
            joinColumns = @JoinColumn(
                    name = "film_id", referencedColumnName = "film_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "category_id", referencedColumnName = "category_id"
            )
    )
    Set<Category> categories;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Year getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Year releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Language getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(Language originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public short getRentalDuration() {
        return rentalDuration;
    }

    public void setRentalDuration(short rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public BigDecimal getRentalRate() {
        return rentalRate;
    }

    public void setRentalRate(BigDecimal rentalRate) {
        this.rentalRate = rentalRate;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public BigDecimal getReplacementCost() {
        return replacementCost;
    }

    public void setReplacementCost(BigDecimal replacementCost) {
        this.replacementCost = replacementCost;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public List<String> getSpecialFeatures() {
        return specialFeatures;
    }

    public void setSpecialFeatures(List<String> specialFeatures) {
        this.specialFeatures = specialFeatures;
    }

    public Set<Actor> getActors() {
        return actors;
    }

    public void setActors(Set<Actor> actors) {
        this.actors = actors;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Film film)) return false;
        if (!super.equals(o)) return false;
        return id == film.id
                && rentalDuration == film.rentalDuration
                && length == film.length
                && Objects.equals(title, film.title)
                && Objects.equals(description, film.description)
                && Objects.equals(releaseYear, film.releaseYear)
                && Objects.equals(language, film.language)
                && Objects.equals(originalLanguage, film.originalLanguage)
                && Objects.equals(rentalRate, film.rentalRate)
                && Objects.equals(replacementCost, film.replacementCost)
                && rating == film.rating
                && Objects.equals(specialFeatures, film.specialFeatures)
                && Objects.equals(actors, film.actors)
                && Objects.equals(categories, film.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, title, description, releaseYear, language, originalLanguage, rentalDuration, rentalRate, length, replacementCost, rating, specialFeatures, actors, categories);
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
