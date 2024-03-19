package com.example.enlaco.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "storageset")
@SequenceGenerator(
        name = "storageset_SEQ",
        sequenceName = "storageset_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class StorageSetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "storageset_SEQ")
    private Integer ssid;                       //저장리스트 목록
    private String ssimg;

}
