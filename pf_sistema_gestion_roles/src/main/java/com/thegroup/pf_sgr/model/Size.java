package com.thegroup.pf_sgr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sizes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_size")
    private Integer id;

    @Column(name = "name_size", unique = true, nullable = false)
    private String name;
}