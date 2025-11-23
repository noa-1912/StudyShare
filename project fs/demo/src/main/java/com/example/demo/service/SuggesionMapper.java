package com.example.demo.service;

import com.example.demo.dto.SuggestionDTO;
import com.example.demo.model.Suggestion;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SuggesionMapper {
UsersMapper INSTANCE= Mappers.getMapper(UsersMapper.class);
    List<SuggestionDTO> map(List<Suggestion> suggestions);
   Suggestion suggestionDTOtoSuggestion(SuggestionDTO suggestion);

    default SuggestionDTO suggestionDto(Suggestion s) throws IOException {
        SuggestionDTO suggestionDTO=new SuggestionDTO();

        suggestionDTO.setUserDTO(INSTANCE.usersToUsersDTO(s.getUser()));
        suggestionDTO.setId(s.getId());
        suggestionDTO.setContent(s.getContent());
        suggestionDTO.setPage(s.getPage());
        suggestionDTO.setExercise(s.getExercise());
        suggestionDTO.setSection(s.getSection());
        suggestionDTO.setSubSection(s.getSubSection());
        suggestionDTO.setUploadDate(s.getUploadDate());
        suggestionDTO.setImagePath(s.getImagePath());

        //החזרת התמונה
        suggestionDTO.setImage(ImageUtils.getImage(s.getImagePath()));
        return suggestionDTO;
    }

}
