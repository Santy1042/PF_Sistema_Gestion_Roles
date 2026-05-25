package com.thegroup.pf_sgr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "colors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_color")
    private Integer id;

    @Column(name = "name_color", unique = true, nullable = false)
    private String name;
}