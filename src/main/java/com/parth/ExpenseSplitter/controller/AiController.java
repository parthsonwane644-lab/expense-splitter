package com.parth.ExpenseSplitter.controller;

import com.parth.ExpenseSplitter.component.CurrentUser;
import com.parth.ExpenseSplitter.model.ExpenseGroup;
import com.parth.ExpenseSplitter.model.user;
import com.parth.ExpenseSplitter.service.ExpenseAiService;
import com.parth.ExpenseSplitter.service.ParsedExpense;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final ExpenseAiService expenseAiService;
    private final CurrentUser currentUser;
    private final GroupController groupController;

    public record TextRequest(@NotBlank String text) {
    }
    @PostMapping("/groups/{groupId}/parse-expense")
    public ParsedExpense parse(@PathVariable Long groupId, @Valid @RequestBody TextRequest req){

        ExpenseGroup g=groupController.getGroupForMember(groupId);
        List<String> names=g.getMembers().stream().map(user::getName).toList();
        return expenseAiService.parse(req.text(),names,currentUser.get().getName());

    }
}
