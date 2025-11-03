package com.example.demo.service;

import com.example.demo.dto.SolutionsDTO;
import com.example.demo.model.Solutions;
import org.mapstruct.Mapper;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface SolutionsMapper{

    default SolutionsDTO solutionsDTO(Solutions s)throws IOException {
        SolutionsDTO solutionsDTO=new SolutionsDTO();
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


