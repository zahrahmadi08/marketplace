package com.ap.marketplace.domain;

import jakarta.persistence.*;

/** شهر محل آگهی. */
@Entity
@Table(name = "cities",
        uniqueConstraints = @UniqueConstraint(name = "uk_city_name", columnNames = "name"))
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    protected City() { }

    public City(String name) { this.name = name; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
