package com.example.demo.controller;

import java.nio.file.Path;
import java.nio.file.Files;

import com.example.demo.model.Users;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.service.ImageUtils;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;

@RestController//מחזירה JSON ולא HTML
@RequestMapping("/api/user")
@CrossOrigin
public class UsersController {
    private UsersRepository usersRepository;//גישה לטבלת USER
    private RoleRepository roleRepository;//גישה לתפקידים ROLE
    private AuthenticationManager authenticationManager;//מאמת סיסמאות ומייל
    private JwtUtils jwtUtils;//יוצא/ מנקה COOKIES של JWT


    @Autowired
    public UsersController(UsersRepository usersRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    //הרשמות
    @PostMapping("/signup")
    public ResponseEntity<Users> signUp(@RequestPart("user") Users user, @RequestPart("image") MultipartFile file) throws IOException {
        //נבדוק שהמייל כבר לא קיים
        Users u = usersRepository.findByEmail(user.getEmail());
        if (u != null)//אם המייל קיים נחזיר תשובה של 400
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ImageUtils.uploadImage(file);//שמירת התמונה בתיקייה
        user.setImagePath(file.getOriginalFilename());//שומרת את התמונת פרופיל למשתמש
        //לפני שמירת הסיסמה נעשה הצפנה
        String pass = user.getPassword();//סיסמה לא מוצפנת
        user.setPassword(new BCryptPasswordEncoder().encode(pass));//סיסמה מוצפנת
        user.setDate(LocalDate.now());//שמירת תאריך עכשיווי

        //מוסיפב לו תפקיד USER
        user.getRoles().add(roleRepository.findById((long) 1).get());
        //שמירת המשתמש במס הנתונים
        usersRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);

    }

    //התחברות
    @PostMapping("/signin")
    //פונקציה מחזירה סטטוס ועוגייה ואת שם המשתמש
    public ResponseEntity<?> signin(@RequestBody Users u) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(u.getEmail(), u.getPassword()));

        //שומר את האימות במערכת האבטחה
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //CustomUserDetails לוקח את פרטי המשתמש ומכניס אותם
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        //יוצר עוגייה עבור המשתמש - מה שמאפשר לו להשאר מחובר
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        //מוצאים את המייל של המשתמש מהDB לראות אם הוא משתמש קיים
        Users fullUser = usersRepository.findByEmail(userDetails.getUsername());
        if (fullUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        //שולחים את העוגיה לאנגולר ומחזירים פרטי משתמש ללא סיסמה עם סטטוס 200
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(fullUser);
    }


    //התנתקות
    @PostMapping("/signout")
    public ResponseEntity<?> signOut() {
        //מוחק את העוגייה
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        //מחזיר עוגייה ריקה, מוחק את החיבור ושולח הודעת התנתקות
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("you've been signed out!");
    }


    //מקבל ושומר תמונה של משתמש עבור הפרופיל
    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<byte[]> getUserImage(@PathVariable String filename) {
        try {
            //בניית הנתיב לקובץ התמונה
            Path imagePath = Paths.get(System.getProperty("user.dir") + "\\images\\" + filename);
            //קריאת קובץ התמונה כמערך של ביטים
            byte[] imageBytes = Files.readAllBytes(imagePath);

            //שולח את התמונה לדפדפן עם סוג התמונה וסטטוס 200
            return ResponseEntity.ok()
                    .header("Content-Type", Files.probeContentType(imagePath))
                    .body(imageBytes);
        } catch (IOException e) {
            //אם אין קובץ מחזיר לא נמצא 404
            System.out.println("❌ שגיאה בקריאת תמונת משתמש: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
