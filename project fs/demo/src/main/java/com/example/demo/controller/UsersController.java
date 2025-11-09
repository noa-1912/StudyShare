package com.example.demo.controller;

import com.example.demo.model.Users;
import com.example.demo.service.RoleRepository;
import com.example.demo.service.SuggestionRepository;
import com.example.demo.service.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@CrossOrigin
public class UsersController {
private  UsersRepository usersRepository;
private RoleRepository roleRepository;

@Autowired
public  UsersController(UsersRepository usersRepository,RoleRepository roleRepository){
    this.usersRepository=usersRepository;
    this.roleRepository=roleRepository;
}
    //הרשמות
    @PostMapping("/signup")
    public ResponseEntity<Users> signUp(@RequestBody Users user){
    //נבדוק ששם משתמש לא קיים
        Users u=usersRepository.findByEmail(user.getEmail());
        if(u!=null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        //לפני שמירצ הסיסמה נעשה הצפנה
        String pass=user.getPassword();//סיסמה לא מוצפנת
        user.setPassword(new BCryptPasswordEncoder().encode(pass));//סיסמה מוצפנת


        user.getRoles().add(roleRepository.findById((long)1).get());
        usersRepository.save(user);
        return new ResponseEntity<>(user,HttpStatus.CREATED);

    }
}
