package com.example.demo.security.jwt;

import com.example.demo.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

//********תפקיד המחלקה:
//פילטר שרץ לפני כל בקשה לשרת
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private CustomUserDetailsService userDetailsService;


    //********תפקיד הפונקציה:
    //מה הפונקציה מקבלת?
    //
    @Override
    //בודקת אם הבקשה מגיעה ממשתמש מחובר
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try{
            //מחפש קוקי בשם אם נמצא מחזיר JWT אם לא מחזיר NULL
            String jwt=jwtUtils.getJwtFromCookies(httpServletRequest);
            //*********מהי השאלה כאן???
            //בדיקה אם הטוקן תקף ולא שבור או מזוייף
            if(jwt !=null && jwtUtils.validateJwtToken(jwt)){
                //שליפת אימייל מתוך JWT
                String userName=jwtUtils.getUserNameFromJwtToken(jwt);
                //טעינת משתמש מהDBבעזרת המייל ומצאים את תפקידיו וכך יודעים מה מותר לו
                UserDetails userDetails= userDetailsService.loadUserByUsername(userName);


                //יצירת אוביקט שמייצג משתמש מחובר ובתוכו פרטי משתמש ללא סיסמה וכל ההרשאות שלו
                UsernamePasswordAuthenticationToken authentication=
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                //החזרת המשתמש למערכת האבטחה
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        //***************מה משמעות ה-filter??
        //מעביר את הבקשה לפילטר הבא בשרשרת עד שמגיעים לקונטרולר
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }

}
