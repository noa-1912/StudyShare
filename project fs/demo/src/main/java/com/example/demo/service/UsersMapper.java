package com.example.demo.service;

import com.example.demo.dto.SolutionsDTO;
import com.example.demo.dto.SuggestionDTO;
import com.example.demo.dto.UsersDTO;
import com.example.demo.model.Solutions;
import com.example.demo.model.Suggestion;
import com.example.demo.model.Users;
import org.mapstruct.Mapper;

import java.io.IOException;

import java.util.List;



@Mapper(componentModel = "spring")
public interface UsersMapper {
    default UsersDTO usersDTO(Users u) throws IOException {
        UsersDTO usersDTO=new UsersDTO();
        usersDTO.setId(u.getId());
        usersDTO.setName(u.getName());
        usersDTO.setEmail(u.getEmail());
        usersDTO.setDate(u.getDate());
        usersDTO.setImagePath(u.getImagePath());
         //החזרת התמונה
        usersDTO.setImage(ImageUtils.getImage(u.getImagePath()));
        return usersDTO;
    }
    UsersDTO usersToUsersDTO(Users u);


}
