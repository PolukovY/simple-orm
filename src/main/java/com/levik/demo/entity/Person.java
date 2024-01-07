package com.levik.demo.entity;

import com.levik.orm.annotation.Column;
import com.levik.orm.annotation.Entity;
import com.levik.orm.annotation.Id;
import com.levik.orm.annotation.Table;
import lombok.Data;

@Data
@Entity
@Table("persons")
public class Person {

    @Id
    private Long id;

    @Column("last_name")
    private String firstName;

    @Column("first_name")
    private String lastName;
}
