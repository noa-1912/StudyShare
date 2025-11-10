package com.example.demo.controller;

import com.example.demo.model.Users;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.service.RoleRepository;
import com.example.demo.service.SuggestionRepository;
import com.example.demo.service.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UsersController {
private  UsersRepository usersRepository;
private RoleRepository roleRepository;
private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;


@Autowired
public  UsersController(UsersRepository usersRepository,RoleRepository roleRepository, AuthenticationManager authenticationManager,JwtUtils jwtUtils){
    this.usersRepository=usersRepository;
    this.roleRepository=roleRepository;
    this.authenticationManager=authenticationManager;
    this.jwtUtils=jwtUtils;
}

    //התחברות
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Users u){
        Authentication authentication=authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(u.getEmail(),u.getPassword()));

        //שומר את האימות
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //CustomUserDetails לוקח את פרטי המשתמש ומכניס אותם
        CustomUserDetails userDetails=(CustomUserDetails)authentication.getPrincipal();

        ResponseCookie jwtCookie=jwtUtils.generateJwtCookie(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,jwtCookie.toString())
                .body(userDetails.getUsername());
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

    //התנתקות
    @PostMapping("/signout")
    public ResponseEntity<?> signOut(){
        ResponseCookie cookie=jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body("you've been signed out!");
    }
}
