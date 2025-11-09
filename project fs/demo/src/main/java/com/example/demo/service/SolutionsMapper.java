package com.example.demo.service;

import com.example.demo.dto.SolutionsDTO;
import com.example.demo.model.Solutions;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SolutionsMapper{
    UsersMapper INSTANCE= Mappers.getMapper(UsersMapper.class);

    List<SolutionsDTO> mapSolutionsDTOList(List<Solutions> list);
SolutionsDTO mapSolutionsDTO(Solutions solutions);

    default SolutionsDTO solutionsDTO(Solutions s)throws IOException {
        SolutionsDTO solutionsDTO=new SolutionsDTO();
       solutionsDTO.setUserDTO(INSTANCE.usersToUsersDTO(s.getUser()));
        solutionsDTO.setId(s.getId());
        solutionsDTO.setContent(s.getContent());
        solutionsDTO.setExercise(s.getExercise());
        solutionsDTO.setPage(s.getPage());
        solutionsDTO.setSubSection(s.getSubSection());
        solutionsDTO.setUploadDate(s.getUploadDate());
        solutionsDTO.setAvg(s.getAvg());
        solutionsDTO.setImage(ImageUtils.getImage(s.getImagePath()));
        return solutionsDTO;
    }

}


