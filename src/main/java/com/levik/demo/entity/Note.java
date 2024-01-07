package com.levik.demo.entity;


import com.levik.orm.annotation.Column;
import com.levik.orm.annotation.Entity;
import com.levik.orm.annotation.Id;
import com.levik.orm.annotation.Table;
import lombok.Data;

@Data
@Entity
@Table("notes")
public class Note {

    @Id
    private Long id;

    @Column("body")
    private String body;

    @Column("person_id")
    private String personId;
}
