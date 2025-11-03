package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageUtils {

    //מחזיר את המיקום של הפרויקט הספציפי
    private static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\images\\";

    //להעלותתמונה לשרת
    public static void uploadImage(MultipartFile file) throws IOException {
        String path=UPLOAD_DIRECTORY+ file.getOriginalFilename();
        Path fileName = Paths.get(path );
        //שומרת ביטים במחשב
        Files.write(fileName, file.getBytes());

    }

    public static String getImage(String path) throws IOException{
        //ישתנה ממחשב למחשב במיקום של התמונה בקובץ UPLOAD_DIRECTORY
        //פשאי לא ישתנה - השם של התונה עם הסיומת
        Path fileName = Paths.get(UPLOAD_DIRECTORY+path);
        byte[] byteImages = Files.readAllBytes(fileName);//מעביר את התמונה למערך ביטים
        return Base64.getEncoder().encodeToString(byteImages);

    }

}
