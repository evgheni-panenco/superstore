package com.university.superstore.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "item")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Integer price;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "description")
    private String description;

    @Column(name = "warehouse_id")
    private String warehouseId;
}
