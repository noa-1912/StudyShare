package com.example.demo.controller;

import com.example.demo.dto.SolutionsDTO;
import com.example.demo.dto.SuggestionDTO;
import com.example.demo.model.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    EmailService emailService;
    SolutionsMapper solutionsMapper;
    SolutionsRepository solutionsRepository;
    private final UsersRepository usersRepository;
    private final BooksRepository booksRepository;
    CommentsRepository commentsRepository;
    private static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\images\\";

    @Autowired
    public SolutionsController(SolutionsRepository solutionsRepository, SolutionsMapper solutionsMapper, UsersRepository usersRepository, BooksRepository booksRepository,CommentsRepository commentsRepository, EmailService emailService) {
        this.solutionsRepository = solutionsRepository;
        this.solutionsMapper = solutionsMapper;
        this.usersRepository = usersRepository;
        this.booksRepository = booksRepository;
        this.commentsRepository=commentsRepository;
        this.emailService=emailService;
    }


    //מחזירה את כל הפתרונות
    @GetMapping("/getSolution")
    public ResponseEntity<List<SolutionsDTO>> getAllSolutions() throws IOException {
        //שליפת כל הבקשות ממסד הנתונים
        List<Solutions> solutions = solutionsRepository.findAll();


        if (solutions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        //המרה כל אוביקט לDTO
        List<SolutionsDTO> dtos = solutions.stream()
                .map(s -> {
                    try {
                        SolutionsDTO dto = solutionsMapper.solutionsDTO(s);

                        //  חישוב ממוצע
                        List<Comments> comments = commentsRepository.findAllBySolutionId(s.getId());
                        double avg = comments.isEmpty()
                                ? 0
                                : comments.stream()
                                .mapToDouble(Comments::getRatingValue)
                                .average()
                                .orElse(0);

                        dto.setAvg(avg);

                        return dto;

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }




    //שליפת פתרון לפי מזהה ID
    @GetMapping("/getSolutions/{id}")
    public ResponseEntity<SolutionsDTO> get(@PathVariable long id) throws IOException {
        Solutions s = (Solutions) solutionsRepository.findById(id).get();//שליםת הפתרון מהDB
        if (s == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);        SolutionsDTO dto = solutionsMapper.solutionsDTO(s);

        //  חישוב ממוצע
        List<Comments> comments = commentsRepository.findAllBySolutionId(s.getId());
        double avg = comments.isEmpty()
                ? 0
                : comments.stream()
                .mapToDouble(Comments::getRatingValue)
                .average()
                .orElse(0);

        dto.setAvg(avg);
        return new ResponseEntity<>(dto, HttpStatus.OK);    }

    //חיפוש פתרון לפי ספר עמוד ותרגיל
    @GetMapping("/searchSolutions/{bookId}/{page}/{exercise}")
    public ResponseEntity<List<SolutionsDTO>> getAllSolutions(
            @PathVariable Long bookId,
            @PathVariable int page,
            @PathVariable int exercise
    ) throws IOException {

        List<Solutions> solutions =
                solutionsRepository//שליפת הפתרון המבוקש - אם קיים
                        .findSolutionsByBook_IdAndPageAndExercise(bookId, page, exercise);

        if (solutions.isEmpty()) {//אם לא נמצא פתרון מחזירם רשימה ריקה
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }


        List<SolutionsDTO> dtos = solutions.stream()//המרת רשימת הפתרונות לDTO
                .map(s -> {
                    try {
                        SolutionsDTO dto = solutionsMapper.solutionsDTO(s);

                        //  חישוב ממוצע
                        List<Comments> comments = commentsRepository.findAllBySolutionId(s.getId());
                        double avg = comments.isEmpty()
                                ? 0
                                : comments.stream()
                                .mapToDouble(Comments::getRatingValue)
                                .average()
                                .orElse(0);

                        dto.setAvg(avg);

                        return dto;                    }
                     catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }


    //העלאת פתרון חדש עם תמונה
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/uploadSolutions")
    public ResponseEntity<SolutionsDTO> uploadSolutionsWithImage(
            @RequestPart("image") MultipartFile file,//מקבלת קובץ תמונה
            @RequestPart("solution") Solutions s) {

        //טעינת המשתמש המעלה את הפתרון
        Users user = usersRepository.findById(s.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        s.setUser(user);

        //טעינת הספר מDB
        Books book = booksRepository.findById(s.getBook().getId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        s.setBook(book);

        try {
            ImageUtils.uploadImage(file);//שמירת התמונה בתיקייה
            s.setImagePath(file.getOriginalFilename());//במסד שומרים רק את שם הקובץ

            Solutions solutions = solutionsRepository.save(s);//שמירת הפתרון במסד
            SolutionsDTO dto = solutionsMapper.solutionsDTO(solutions);//ממיר פתרון לDTO

            //אנגולר מקבל JSON עם הפתרון + תמונה בקידוד
            return new ResponseEntity<>(dto, HttpStatus.CREATED);

        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





    //העלאת פתרון חדש עם תמונה
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/uploadSolutionsWithEmail", consumes = "multipart/form-data")
    public ResponseEntity<SolutionsDTO> uploadSolutionsWithImageWithEmail(
            @RequestPart(value = "image", required = false) MultipartFile file,
            @RequestPart("solution") Solutions s,
            @RequestParam("email") String email) {

        //טעינת המשתמש המעלה את הפתרון
        Users user = usersRepository.findById(s.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        s.setUser(user);

        //טעינת הספר מDB
        Books book = booksRepository.findById(s.getBook().getId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        s.setBook(book);

        try {
            ImageUtils.uploadImage(file);//שמירת התמונה בתיקייה
            s.setImagePath(file.getOriginalFilename());//במסד שומרים רק את שם הקובץ

            Solutions solutions = solutionsRepository.save(s);//שמירת הפתרון במסד
            SolutionsDTO dto = solutionsMapper.solutionsDTO(solutions);//ממיר פתרון לDTO

            // ====================  ✉ שליחת מייל אוטומטית ✉ ======================
            String ownerEmail =email ; // מעלה הבקשה
            String solverName = user.getName(); // פותר השאלה

            emailService.sendEmail(
                    ownerEmail,
                    "📌 קיבלת פתרון חדש לבקשה שלך!",
                    "שלום! \nמשתמש בשם " + solverName + " העלה פתרון לבקשה שלך באתר 🧩\n" +
                            "כנס עכשיו לצפות בפתרון 👉 StudyShare"
            );


            //אנגולר מקבל JSON עם הפתרון + תמונה בקידוד
            return new ResponseEntity<>(dto, HttpStatus.CREATED);

        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//מחיקה ניתנת רק למשתמש שהעלה את הפתרון
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/deleteSolution/{id}")
    public ResponseEntity deleteSolutionById(@PathVariable Long id){
        try{
            if(solutionsRepository.existsById(id)){
                solutionsRepository.deleteById(id);
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(HttpStatus. NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
