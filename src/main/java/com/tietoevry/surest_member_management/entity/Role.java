package com.tietoevry.surest_member_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name",length = 50,nullable = false,unique = true)
    private String name;
}
