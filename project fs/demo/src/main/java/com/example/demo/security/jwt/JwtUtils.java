package com.example.demo.security.jwt;

import com.example.demo.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.util.Date;

// תפקיד המחלקה:
// 1. ליצור JWT עבור משתמש מחובר
// 2. לשמור את ה-JWT בתוך Cookie בדפדפן
// 3. להוציא את האימייל (username) מתוך ה-JWT
// 4. לבדוק אם JWT שהגיע מהלקוח תקף ולא פג תוקף
// 5. למחוק את ה-JWT מה-Cookie בזמן התנתקות (signout)
@Component
public class JwtUtils {

    //********תפקיד הפונקציה:
    //לשלוף JWT מתוך קוקי של משתמש
    ///////מה הפונקציה מקבלת?/
    //מקבלת בקשה מהדפדפן
    ///////מה הפונקציה מחזירה?/
    //מחזירה JWT מתוך העוגייה רק אם מחובר
    public String getJwtFromCookies(HttpServletRequest request) {
        //כל בקשה לשרת עוברת דרך פילטר לבדוק אם יש שם קוקי
        Cookie cookie = WebUtils.getCookie(request, "securitySample");
        if (cookie != null) {//אם נמצא קוקי
            return cookie.getValue();//מוציא ממנו את הטוקן
        } else {//אם לא נמצא קוקי
            return null;//המשתמש לא מחובר
        }
    }
    //********תפקיד הפונקציה:
    //לקרוא את המייל מתוך הJWT
    //לזהות מי המשתמש ע"י הטוקן JWT ולהזחיר את האימייל של המשתמש
    ///////מה הפונקציה מקבלת?/
    //מקבלת token - jwt
    ///////מה הפונקציה מחזירה?/
    //מחזירה את האימייל של המשתמש
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();

    }
    //********תפקיד הפונקציה:
    //לוודא אם הJWT תקף
    ///////מה הפונקציה מקבלת?/
    //רצף תווים של טוקן JWT
    ///////מה הפונקציה מחזירה?/
    //אם חוקי - TRUE אם לא חוקי - FALSE
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            //טוקן שבור
            System.out.println("Invalid jwt token " + e.getMessage());
        } catch (ExpiredJwtException e) {
            //פג תוקף
            System.out.println("jwt is expired " + e.getMessage());
        } catch (IllegalArgumentException e) {
            //טוקן ריק
            System.out.println("JWT claims string is empty " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            //פורמט לא נתמך
            System.out.println("JWT token is unsupported " + e.getMessage());
        }
        catch (SignatureException e) {
            //חתימה לא תואמת של האימיל
            System.out.println("Signature is wrong " + e.getMessage());
        }
        return false;
    }
    //********תפקיד הפונקציה:
    //יצירת JWT חדש בזמן ההתחברות
    ///////מה הפונקציה מקבלת?/
    //מקבלת אימייל
    /////מה הפונקציה מחזירה?/
    // מחזירה טוקן JWT רגיל
    public String generateTokenFromUsername(String username) {

        return Jwts.builder()//הטוקן מכיל:
                .setSubject(username)//אימייל
                .setIssuedAt(new Date())//מתי נוצר
                .setExpiration(new Date((new Date()).getTime()+86400000))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact(); //חתימה דיגיטלית
    }
    //********תפקיד הפונקציה:
    //ליצור מפתח סודי שישמש לחתימת הJWT
    ///////מה הפונקציה מחזירה?/
    //מחזירה את המפתח הסודי שבו חותמים את JWT
    //כל מי שאין לו את המפתח לא יכול לשנות/לזייף טוקן
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode("=============================================sec=============================================================================================================================="));
    }

    //********תפקיד הפונקציה:
    //ליצור קוקי שמכיל JWT אחרי שמשתמש התחבר
    ///////מה הפונקציה מקבלת?/
    //שם ואימייל משתמש
    ///////מה הפונקציה מחזירה?/
    //מחזירה קוקי אמיתי ששמים בדפדפן עם שם ותוקף
    public ResponseCookie generateJwtCookie(CustomUserDetails userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        ResponseCookie cookie = ResponseCookie.from("securitySample", jwt)
                .path("/api").maxAge(24*60*60).httpOnly(true).build();
        return cookie;
    }
    //********תפקיד הפונקציה:
    //למחוק JWT מהדפדפן בהתנתקות
    //למחוק JWT במוך קוקי בזמן ההתנתקות
    //מה הפונקציה מקבלת?
    ///////מה הפונקציה מחזירה?/
    /// מחזירה קוקי עם ערך NULL עבור ההתנתקות
    public ResponseCookie getCleanJwtCookie(){
        ResponseCookie cookie= ResponseCookie.from("securitySample",null).path("/api").build();
        return cookie;
    }
}
