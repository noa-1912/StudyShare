package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class AIChatService {
    private final ChatClient chatClient;//האוביקט שמדבר עם מודל הAI
    private final ChatMemory chatMemory;//שומר את היסטוריית השיחה
    private final static String SYSTEM_INSTRUCTION = """
            אתה עוזר לימודי באתר Study-Share. מטרתך לעזור לתלמידים להבין חומר לימודי במתמטיקה ובאנגלית (כיתות ט–י"ב).
            
            חוקים:
            1. תמיד לענות בעברית ברורה, פשוטה, ובגובה של תלמיד.
            2. לתת הסברים צעד-אחר-צעד, במיוחד בחומר מתמטי.
            3. לא לתת תשובות מלאות לתרגילים המוגנים בזכויות יוצרים (כמו ספרי יואל גבע). במקום זה:
               - לתת רמזים,
               - להסביר את החומר,
               - להביא דוגמה דומה,
               - לכוון את התלמיד לפתרון.
            4. להתאים את ההסבר למקצוע (מתמטיקה/אנגלית) ולשכבה אם צוין.
            5. מותר להשתמש בנוסחאות, תרשימים מילוליים, הסבר רעיוני, ופירוק שלבים.
            6. אם השאלה לא ברורה — לבקש הבהרה.
            7. לא לענות על שאלות שאינן קשורות ללמידה, או שאלות אישיות.
            8. לעודד חשיבה עצמאית: לתת קודם רמז, ואז פתרון חלקי במידת הצורך.
            
            יכולות עיקריות:
            - להסביר נושאים במתמטיקה (אלגברה, גיאומטריה, טריגונומטריה, סדרות, פונקציות, חדו"א).
            - ליצור שאלות תרגול ולבדוק תשובות.
            - להסביר אנגלית ברמה תיכונית: אוצר מילים, דקדוק, כתיבה, הבנת הנקרא.
            - לתת הסברים מסודרים: הגדרה → רעיון → דוגמה → תרגול קצר.
            
            כל תשובה צריכה להיות קצרה, ברורה, וממוקדת בלמידה.
            
            """;

    public AIChatService(ChatClient.Builder chatClient, ChatMemory chatMemory) {
        this.chatClient = chatClient.build();
        this.chatMemory = chatMemory;
    }

    //תשובה רגילה בלי זכרון ללא STREAMING
    public String getResponse(String prompt) {
        SystemMessage systemMessage = new SystemMessage(SYSTEM_INSTRUCTION);//מוסיפים את חוקי הצאט
        UserMessage userMessage = new UserMessage(prompt);//מוסיפים את הודעת המשתמש

        List<Message> messageList = List.of(systemMessage, userMessage);//שולחים הכל לAI

        return chatClient.prompt().messages(messageList).call().content();//מחזירים רגיל את התשובה

    }

    //תשובה רגילה + זכרון שיחה
    public String getResponse2(String prompt, String conversationId) {
        List<Message> messageList = new ArrayList<>();
        messageList.add(new SystemMessage(SYSTEM_INSTRUCTION));//מוסיפים את הודעת המשתמש
        messageList.addAll(chatMemory.get(conversationId));
        UserMessage userMessage = new UserMessage(prompt);
        messageList.add(userMessage);
        String aiResponse = chatClient.prompt().messages(messageList).call()
                .content();
        AssistantMessage aiMessage = new AssistantMessage(aiResponse);
        List<Message> messageList1 = List.of(userMessage, aiMessage);
        chatMemory.add(conversationId, messageList1);
        return aiResponse;

    }



    //תשובה עם זכרון + STREAMING
    public Flux<String> streamResponse(String prompt, String conversationId) {

        // בונים את הסטוריית הצאט + החוקים + ההודעה החדשה מהמשתמש
        List<Message> history = new ArrayList<>();
        history.add(new SystemMessage(SYSTEM_INSTRUCTION));
        history.addAll(chatMemory.get(conversationId));
        UserMessage userMessage = new UserMessage(prompt);
        history.add(userMessage);

        //התשובה המליאה
        StringBuilder full = new StringBuilder();

        return chatClient
                .prompt()
                .messages(history)
                .stream()   // ← זה מפעיל STREAMING
                .content()  // ← Flux<String>
                .doOnNext(full::append)
                .doOnComplete(() -> {
                    //שמירת התשובה בזכרון
                    AssistantMessage aiMsg = new AssistantMessage(full.toString());
                    chatMemory.add(conversationId, List.of(userMessage, aiMsg));
                });
    }


}