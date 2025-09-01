package com.Tienld.diary_project.dto.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends AbstractDTO<UserDTO> {

    private String username;
    private String fullname;
    private String email;
    private List<DiaryDTO> diaryDTOS;
    private List<ChatMessageDTO> chatMessageDTOS;

}
