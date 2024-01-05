package com.example.enlaco.DTO;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDTO {
    private Integer     mid;        //번호
    @NotBlank(message = "이메일을 입력하세요")
    private String      memail;     //회원이메일
    private String      mpwd;       //회원비번
    private String      mnick;      //닉네임
    private String      mphone;     //폰번호
    private String      mbirth;     //생년월일
    private LocalDateTime regDate;
}
