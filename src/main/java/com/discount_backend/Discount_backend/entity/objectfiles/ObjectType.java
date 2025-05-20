package com.discount_backend.Discount_backend.entity.objectfiles;


import lombok.Getter;

@Getter
public enum ObjectType {
    CATEGORY(1),
    MARKET(2),
    DISCOUNT(3),
    USER(4);

    private final int id;
    ObjectType(int id) { this.id = id; }
}
