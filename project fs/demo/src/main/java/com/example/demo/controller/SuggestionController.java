//package com.example.demo.controller;
//
//
//import com.example.demo.dto.SuggestionDTO;
//import com.example.demo.model.Suggestion;
//import com.example.demo.model.Users;
//import com.example.demo.service.ImageUtils;
//import com.example.demo.service.SuggesionMapper;
//import com.example.demo.service.SuggestionRepository;
//import com.example.demo.service.UsersRepository;
//import org.springframework.transaction.annotation.Transactional; // ⬅️ זה הנדרש
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//
//@RequestMapping("api/suggesion")
//@RestController
//@CrossOrigin
//public class SuggestionController {
//
//    SuggestionRepository suggestionRepository;
//    SuggesionMapper suggesionMapper;
//    UsersRepository usersRepository;
//
//    @Autowired//Spring “מכניס” עבורך את האובייקט הדרוש מבלי שתצטרכי ליצור אותו בעצמך עם new.
//    public SuggestionController(SuggestionRepository suggestionRepository, SuggesionMapper suggesionMapper,UsersRepository usersRepository) {
//        this.suggestionRepository = suggestionRepository;
//        this.suggesionMapper = suggesionMapper;
//        this.usersRepository=usersRepository;
//    }
//
//    //מחזירה בקשה לפי ID
//    @GetMapping("/getSuggestion/{id}")
//    @Transactional
//    public ResponseEntity<SuggestionDTO> get(@PathVariable long id) throws IOException {
//        Suggestion s = suggestionRepository.findById(id).get();
//        if (s != null)
//            return new ResponseEntity<>(suggesionMapper.suggestionDto(s), HttpStatus.OK);
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//    //מחזירה את כל הבקשות
//    @GetMapping("/getSuggestion")
//    public ResponseEntity<List<SuggestionDTO>> getAllSuggestions() throws IOException {
//        List<Suggestion> suggestions = suggestionRepository.findAll();
//        if (suggestions.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } List<SuggestionDTO> dtos = suggestions.stream()
//                .map(s -> {
//                    try {
//                        return suggesionMapper.suggestionDto(s);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .toList();
//        return new ResponseEntity<>(dtos, HttpStatus.OK);
//    }
//
//
//    //העלאת פתרון חדש עם בחירת תמונה מהמחשב
//    @PostMapping("/uploadSuggestion")
//    public ResponseEntity<Suggestion> uploadSaggestionWithImage(
//            @RequestPart("image") MultipartFile file,
//            @RequestPart("suggestion") Suggestion s) {
//        Users user = usersRepository.findById(s.getUser().getId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        s.setUser(user);
//
//        try {
//            //כאן נשמר התמונה כולל הסיומת שלו
//            System.out.println("📦 File = " + (file != null ? file.getOriginalFilename() : "null"));
//            System.out.println("🧾 Suggestion = " + s.getContent());
//            System.out.println("👤 User = " +
//                    (s.getUser() != null
//                            ? (s.getUser().getEmail() != null ? s.getUser().getEmail() : "user without email")
//                            : "no user"));
//
//            ImageUtils.uploadImage(file);
//            s.setImagePath(file.getOriginalFilename());//שמירת שם התומנה עם הסיומת שלה לל
//            Suggestion suggestion = suggestionRepository.save(s);
//            return new ResponseEntity<>(suggestion, HttpStatus.CREATED);
//
//        } catch (IOException e) {
//            System.out.println(e);
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//    @GetMapping("/image/{filename:.+}")
//    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
//        try {
//            // שימי כאן את הנתיב שבו התמונות באמת נשמרות
//            Path imagePath = Paths.get("C:\\Users\\Yael\\Desktop\\StudyShare\\images\\" + filename);
//            byte[] imageBytes = Files.readAllBytes(imagePath);
//
//            return ResponseEntity.ok()
//                    .header("Content-Type", Files.probeContentType(imagePath))
//                    .body(imageBytes);
//        } catch (IOException e) {
//            System.out.println("❌ שגיאה בקריאת תמונה: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }
//
//
//
//
//
//}

package com.example.demo.controller;

import com.example.demo.dto.SuggestionDTO;
import com.example.demo.model.Books;
import com.example.demo.model.Suggestion;
import com.example.demo.model.Users;
import com.example.demo.service.ImageUtils;
import com.example.demo.service.SuggesionMapper;
import com.example.demo.service.SuggestionRepository;
import com.example.demo.service.UsersRepository;
import com.example.demo.service.BooksRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RequestMapping("api/suggesion")
@RestController
@CrossOrigin
public class SuggestionController {

    private final SuggestionRepository suggestionRepository;
    private final SuggesionMapper suggesionMapper;
    private final UsersRepository usersRepository;
    private final BooksRepository booksRepository;

    @Autowired
    public SuggestionController(SuggestionRepository suggestionRepository,
                                SuggesionMapper suggesionMapper,
                                UsersRepository usersRepository,
                                BooksRepository booksRepository) {

        this.suggestionRepository = suggestionRepository;
        this.suggesionMapper = suggesionMapper;
        this.usersRepository = usersRepository;
        this.booksRepository = booksRepository;
    }

    // ============================
    //   getSuggestion by ID
    // ============================
    @GetMapping("/getSuggestion/{id}")
    @Transactional
    public ResponseEntity<SuggestionDTO> get(@PathVariable long id) throws IOException {
        Suggestion s = suggestionRepository.findById(id).orElse(null);
        if (s != null)
            return new ResponseEntity<>(suggesionMapper.suggestionDto(s), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ============================
    //   getAllSuggestions
    // ============================
    @GetMapping("/getSuggestion")
    public ResponseEntity<List<SuggestionDTO>> getAllSuggestions() throws IOException {

        List<Suggestion> suggestions = suggestionRepository.findAll();

        // ❗ מסנן Solutions
        List<Suggestion> onlySuggestions = suggestions.stream()
                .filter(s -> !(s instanceof com.example.demo.model.Solutions))
                .toList();

        if (onlySuggestions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<SuggestionDTO> dtos = onlySuggestions.stream()
                .map(s -> {
                    try {
                        return suggesionMapper.suggestionDto(s);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }


    // ============================
    //   uploadSuggestion + image
    // ============================
//@PreAuthorize("hasRole('ROLE_USER')")

    @PostMapping("/uploadSuggestion")
    public ResponseEntity<SuggestionDTO> uploadSaggestionWithImage(
            @RequestPart("image") MultipartFile file,
            @RequestPart("suggestion") Suggestion s) {

        // ----- Load User -----
        Users user = usersRepository.findById(s.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        s.setUser(user);

        // ----- Load Book -----  (THIS FIXES THE BOOK NAME)
        Books book = booksRepository.findById(s.getBook().getId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        s.setBook(book);

        try {
            ImageUtils.uploadImage(file);
            s.setImagePath(file.getOriginalFilename());

//            Suggestion suggestion = suggestionRepository.save(s);
//            return new ResponseEntity<>(suggestion, HttpStatus.CREATED);
            Suggestion saved = suggestionRepository.save(s);
            SuggestionDTO dto = suggesionMapper.suggestionDto(saved);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);

        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ============================
    //   Get Image
    // ============================
    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get("C:\\Users\\Yael\\Desktop\\StudyShare\\images\\" + filename);
            byte[] imageBytes = Files.readAllBytes(imagePath);

            return ResponseEntity.ok()
                    .header("Content-Type", Files.probeContentType(imagePath))
                    .body(imageBytes);

        } catch (IOException e) {
            System.out.println("❌ שגיאה בקריאת תמונה: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
