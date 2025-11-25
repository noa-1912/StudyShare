package com.example.demo.controller;

import com.example.demo.dto.SolutionsDTO;
import com.example.demo.dto.SuggestionDTO;
import com.example.demo.model.Books;
import com.example.demo.model.Solutions;
import com.example.demo.model.Suggestion;
import com.example.demo.service.ImageUtils;
import com.example.demo.service.SolutionsMapper;
import com.example.demo.service.SolutionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("api/solution")
@RestController
@CrossOrigin
public class SolutionsController {

    SolutionsMapper solutionsMapper;
    SolutionsRepository solutionsRepository;

    private static  String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\images\\";

    @Autowired
    public SolutionsController(SolutionsRepository solutionsRepository, SolutionsMapper solutionsMapper) {
        this.solutionsRepository = solutionsRepository;
        this.solutionsMapper= solutionsMapper;
    }

    @GetMapping("/getSolutions/{id}")
    public ResponseEntity<SolutionsDTO> get(@PathVariable long id) throws IOException {
        Solutions s = (Solutions) solutionsRepository.findById(id).get();
        if(s!=null)
            return new ResponseEntity<>(solutionsMapper.solutionsDTO(s), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    @PostMapping("/uploadSolutions")
    public ResponseEntity<Solutions> uploadSolutionsWithImage(
            @RequestPart("image") MultipartFile file,
            @RequestPart("suggestion") Solutions s) {
        try {
            ImageUtils.uploadImage(file);
            s.setImagePath(file.getOriginalFilename());
            Solutions solutions = solutionsRepository.save(s);
            return new ResponseEntity<>(solutions, HttpStatus.CREATED);
        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }



    @GetMapping("/searchSolutions/{bookId}/{page}/{exercise}")
    public ResponseEntity<List<SolutionsDTO>> getAllSolutions(
            @PathVariable Long bookId,
            @PathVariable int page,
            @PathVariable int exercise
    ) throws IOException {

        List<Solutions> solutions =
                solutionsRepository.findSolutionsByBook_IdAndPageAndExercise(bookId, page, exercise);
        if (solutions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

}
