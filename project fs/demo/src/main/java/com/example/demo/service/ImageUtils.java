package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

//מחלקת עזר להעלות תמונה לשרת
// ולהזחיר תמונה לDTO
public class ImageUtils {

    //מחזיר את המיקום של הפרויקט הספציפי
    private static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\images\\";

    //להעלות תמונה לשרת - המקום בו נשמרות התמונות
    public static void uploadImage(MultipartFile file) throws IOException {
        String path=UPLOAD_DIRECTORY+ file.getOriginalFilename();
        Path fileName = Paths.get(path );
        //שומרת ביטים במחשב
        Files.write(fileName, file.getBytes());
    }

    //מחזיר קובץ שמור בבייס 64
    public static String getImage(String path) throws IOException{
        //ישתנה ממחשב למחשב במיקום של התמונה בקובץ UPLOAD_DIRECTORY
        //פשאי לא ישתנה - השם של התונה עם הסיומת
        Path fileName = Paths.get(UPLOAD_DIRECTORY+path);//מציאת הקובץ בדיסק
        byte[] byteImages = Files.readAllBytes(fileName);//מעביר את התמונה למערך ביטים
        return Base64.getEncoder().encodeToString(byteImages);//הזחרת התמונה בבייס 64

    }

}
