package com.university.superstore.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "orders")
public class Orders implements Serializable {

    @Id
    @Column(name = "order_id")
    private String id;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "created_on")
    private OffsetDateTime createdOn;
}
