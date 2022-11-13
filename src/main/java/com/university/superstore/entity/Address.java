package com.university.superstore.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "address")
public class Address {

    @Id
    @Column(name = "address_id")
    private String id;

    @Column(name = "city")
    private String city;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "country_id")
    private String countryId;
}
