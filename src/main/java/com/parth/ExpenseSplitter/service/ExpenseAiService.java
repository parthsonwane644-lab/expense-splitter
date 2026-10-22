package com.parth.ExpenseSplitter.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExpenseAiService {

    private ChatClient chat ;
    public ExpenseAiService(ChatClient.Builder builder) {
        this.chat = builder.build();
    }

    public ParsedExpense parse(String text,List<String> memberNames,String currentUserName){
        String system = "Extract ONE expense from the user's sentence. "
                + "Use ONLY these member names: " + String.join(", ", memberNames) + ". "
                + "If the payer says I or me, the payer is " + currentUserName + ". "
                + "If nobody is named as sharing, all members share. "
                + "If a value is missing, use null.";
        return chat.prompt()
                .system(system)
                .user(text)
                .call()
                .entity(ParsedExpense.class);
    }
}
