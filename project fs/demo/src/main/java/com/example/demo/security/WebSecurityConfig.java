
package com.example.demo.security;


import com.example.demo.security.jwt.AuthEntryPointJwt;
import com.example.demo.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

//הגדרות אבטחה
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Qualifier("customUserDetailsService")
    CustomUserDetailsService userDetailsService;//יודע לטעון משתמשים לפי אימייל מהDB
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;//אם משתמש לא מחובר מחזיר 401


    public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean//מוודא JWT לפני כל בקשה
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    //********תפקיד הפונקציה:
    //טוען משתמשים ומשווה סיסמאות
    //מה הפונקציה מחזירה?
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    //מצפין סיסמאות לפני שנשמר בDB
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //********תפקיד הפונקציה:
    //מגדירה את שרשרת מסנן האבטחה
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //משבית את הגנת CSRF על ידי הפעלת שיטת `csrf()` והשבתתה
        http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
                    corsConfiguration.setAllowedMethods(List.of("*"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);//לאפשר עוגיות
                    return corsConfiguration;
                }))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                                auth.requestMatchers("/h2-console/**").permitAll()
                                        .requestMatchers("/api/user/signin").permitAll()//התחברות פתוח
                                        .requestMatchers("/api/user/signup").permitAll()//הרשמה פתוח
                                        .requestMatchers("/api/user/**").permitAll()
                                        .requestMatchers("/api/book/**").permitAll()
                                        .requestMatchers("/api/ai/chat/**").permitAll()
                                        .requestMatchers("/api/solution/getSolutions/**").permitAll()

                                        .requestMatchers("/api/solution/searchSolutions/**").permitAll()
                                        .requestMatchers("/api/solution/getSolutions").permitAll()
                                        .requestMatchers("/api/solution/image").permitAll()
                                        .requestMatchers("/api/suggesion/image").permitAll()
                                        .requestMatchers("/api/suggesion/getSuggestion/**").permitAll()
                                        .requestMatchers("/api/suggesion/getSuggestion").permitAll()
                                        .requestMatchers("/api/suggesion/deleteSuggestion/**").permitAll()
                                        .requestMatchers("/api/solution/getSolution").permitAll()
                                       .requestMatchers("/api/comments/getComments/**").permitAll()
                                        .requestMatchers("/api/solution/getSolutions/").permitAll()
                                        //כאן נעשה אפשור לפונקציות של הכניסה, הרשמה
                                        .requestMatchers("/error").permitAll()
//                                        //כל שאר הפונקציות ישארו חסומות אך ורק למשתמשים שנכנסו
//                                        //אם רוצים אפשר לאפשר פונקציות מסוימות או קונטרולים מסוימים לכל המשתמשים
                                        .anyRequest().authenticated()
                );

        //אם אין JWT מחזיר שגיאה401
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));

        // fix H2 database console: Refused to display ' in a frame because it set 'X-Frame-Options' to 'deny'
        http.headers(headers -> headers.frameOptions(frameOption -> frameOption.sameOrigin()));

        http.authenticationProvider(authenticationProvider());


        //***********משמעות הגדרה זו:
        //בדיקת JWT לפני נסיונות התחבברות והרשאות
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
