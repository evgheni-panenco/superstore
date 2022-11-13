package com.university.superstore.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "warehouse")
public class Warehouse {

    @Id
    @Column(name = "warehouse_id")
    private String id;

    @Column(name = "address_id")
    private String address_id;

    @Column(name = "squares")
    private Integer squares;
}
