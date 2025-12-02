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

    //מחזירה בקשה לפי מזהה ID
    @GetMapping("/getSuggestion/{id}")
    @Transactional
    public ResponseEntity<SuggestionDTO> get(@PathVariable long id) throws IOException {
        Suggestion s = suggestionRepository.findById(id).orElse(null);//חיפוש הבקשה לפי הID
        if (s != null)
            return new ResponseEntity<>(suggesionMapper.suggestionDto(s), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //מחזירה את כל הבקשות
    @GetMapping("/getSuggestion")
    public ResponseEntity<List<SuggestionDTO>> getAllSuggestions() throws IOException {
        //שליפת כל הבקשות ממסד הנתונים
        List<Suggestion> suggestions = suggestionRepository.findAll();

        // מסנן Solutions - שיופיע ברשימה רק בקשות ולא פתרונות
        List<Suggestion> onlySuggestions = suggestions.stream()
                .filter(s -> !(s instanceof com.example.demo.model.Solutions))
                .toList();

        if (onlySuggestions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        //המרה כל אוביקט לDTO
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

/// ////////////////////////////////////
    //העלאת בקשה חדשה
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/uploadSuggestion")
    public ResponseEntity<SuggestionDTO> uploadSaggestionWithImage(
            @RequestPart("image") MultipartFile file,
            @RequestPart("suggestion") Suggestion s) {

        // בדיקה אם המשתמש קיים
        Users user = usersRepository.findById(s.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        s.setUser(user);//אם נמצא משתמש מחברים אותו לSUGGESTION שנשמר

        // בדיקה אם הספר קיים כי אנגולר שולח רק ID של ספר ולא את כל הספר
        Books book = booksRepository.findById(s.getBook().getId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        s.setBook(book);

        try {
            ImageUtils.uploadImage(file);//מעלים תמונה מהבקשה
            s.setImagePath(file.getOriginalFilename());

            Suggestion saved = suggestionRepository.save(s);//שומרים את התמונה במסד הנתונים
            SuggestionDTO dto = suggesionMapper.suggestionDto(saved);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);//החזרת אוביקט הבקשה בDTO לFRONTED

        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  מחזיר תמונה לפי השם
//    @GetMapping("/image/{filename:.+}")
//    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
//        try {
//            Path imagePath = Paths.get("C:\\Users\\Yael\\Desktop\\StudyShare\\images\\" + filename);
//            byte[] imageBytes = Files.readAllBytes(imagePath);
//
//            return ResponseEntity.ok()
//                    .header("Content-Type", Files.probeContentType(imagePath))
//                    .body(imageBytes);
//
//        } catch (IOException e) {
//            System.out.println("❌ שגיאה בקריאת תמונה: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }

    //מחיקת בקשה
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/deleteSuggestion/{id}")
    public ResponseEntity deleteSuggestionById(@PathVariable Long id){
        try{
            if(suggestionRepository.existsById(id)){
                suggestionRepository.deleteById(id);
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(HttpStatus. NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
