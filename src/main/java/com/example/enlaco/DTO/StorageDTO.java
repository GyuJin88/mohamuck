package com.example.enlaco.DTO;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StorageDTO {
    private Integer sid;
    private String simg;
    @NotBlank(message = "재료명을 입력하세요")
    private String singre;
    private String sbuydate;
    private String syutong;
    private Integer squan;
    private String skeep;
    private Integer rid;
    private Integer mid;
    private Integer userid;     //oauth2 로그인
    private String dDay;
    private String dDayOver; //유통기한 지남 뱃지
}
