package ru.zverev.rksp4.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@AllArgsConstructor
public class Salad {
    //Zverev
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String type;
    private String name;
    private Float weight;
    private Float price;
    private Boolean isNewYearMood;
    private String chef;
}
