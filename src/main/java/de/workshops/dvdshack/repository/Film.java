package de.workshops.dvdshack.repository;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class Film extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILM_ID_SEQ")
    @SequenceGenerator(name = "FILM_ID_SEQ", sequenceName = "film_film_id_seq", allocationSize = 1)
    @Column(name = "film_id", nullable = false)
    @ToString.Include
    private int id;

    @Column(nullable = false)
    @ToString.Include
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

    @Convert(converter = RatingEnumValueConverter.class)
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
    private Set<Actor> actors;

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
    private Set<Category> categories;

}
