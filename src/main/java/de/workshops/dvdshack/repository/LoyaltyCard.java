package de.workshops.dvdshack.repository;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class LoyaltyCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int points;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoyaltyCard loyaltyCard)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id, loyaltyCard.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString() {
        return "LoyaltyCard{" +
                "id=" + id +
                ", points='" + points + '\'' +
                '}';
    }
}
