package com.example.demo.controller;

import com.example.demo.dto.SolutionsDTO;
import com.example.demo.dto.SuggestionDTO;
import com.example.demo.model.Books;
import com.example.demo.model.Solutions;
import com.example.demo.model.Suggestion;
import com.example.demo.model.Users;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RequestMapping("api/solution")
@RestController
@CrossOrigin
public class SolutionsController {

    SolutionsMapper solutionsMapper;
    SolutionsRepository solutionsRepository;
    private final UsersRepository usersRepository;
    private final BooksRepository booksRepository;
    private static  String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\images\\";

    @Autowired
    public SolutionsController(SolutionsRepository solutionsRepository, SolutionsMapper solutionsMapper,UsersRepository usersRepository,BooksRepository booksRepository) {
        this.solutionsRepository = solutionsRepository;
        this.solutionsMapper= solutionsMapper;
        this.usersRepository=usersRepository;
        this.booksRepository=booksRepository;
    }

    @GetMapping("/getSolutions/{id}")
    public ResponseEntity<SolutionsDTO> get(@PathVariable long id) throws IOException {
        Solutions s = (Solutions) solutionsRepository.findById(id).get();
        if(s!=null)
            return new ResponseEntity<>(solutionsMapper.solutionsDTO(s), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

 @GetMapping("/searchSolutions/{bookId}/{page}/{exercise}")
    public ResponseEntity<List<SolutionsDTO>> getAllSolutions(
            @PathVariable Long bookId,
            @PathVariable int page,
            @PathVariable int exercise
    ) throws IOException {

        List<Solutions> solutions =
                solutionsRepository.findSolutionsByBook_IdAndPageAndExercise(bookId, page, exercise);
//        if (solutions.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
     if (solutions.isEmpty()) {
         return new ResponseEntity<>(List.of(), HttpStatus.OK);
     }


     List<SolutionsDTO> dtos = solutions.stream()
                .map(s -> {
                    try {
                        return solutionsMapper.solutionsDTO(s);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

//    @PostMapping("/uploadSolutions")
//    public ResponseEntity<Solutions> uploadSolutionsWithImage(
//            @RequestPart("image") MultipartFile file,
//            @RequestPart("suggestion") Solutions s) {
//        try {
//            ImageUtils.uploadImage(file);
//            s.setImagePath(file.getOriginalFilename());
//            Solutions solutions = solutionsRepository.save(s);
//            return new ResponseEntity<>(solutions, HttpStatus.CREATED);
//        } catch (IOException e) {
//            System.out.println(e);
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//
//    }
@PostMapping("/uploadSolutions")
public ResponseEntity<SolutionsDTO> uploadSolutionsWithImage(
        @RequestPart("image") MultipartFile file,
        @RequestPart("solution") Solutions s) {

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

        Solutions solutions = solutionsRepository.save(s);
        SolutionsDTO dto = solutionsMapper.solutionsDTO(solutions);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);

    } catch (IOException e) {
        System.out.println(e);
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

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
