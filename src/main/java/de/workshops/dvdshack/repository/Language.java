package de.workshops.dvdshack.repository;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Language extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LANGUAGE_ID_SEQ")
    @SequenceGenerator(name = "LANGUAGE_ID_SEQ", sequenceName = "language_language_id_seq", allocationSize = 1)
    @Column(name = "language_id", nullable = false)
    private Integer id;

    @Column(columnDefinition = "bpchar", length = 20)
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Language language)) return false;
        return Objects.equals(id, language.id) && Objects.equals(name, language.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Language{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
