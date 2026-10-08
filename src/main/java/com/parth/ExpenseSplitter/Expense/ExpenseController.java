package com.parth.ExpenseSplitter.Expense;

import com.parth.ExpenseSplitter.Group.ExpenseGroup;
import com.parth.ExpenseSplitter.Group.GroupRepo;
import com.parth.ExpenseSplitter.user.UserRepo;
import com.parth.ExpenseSplitter.user.user;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseRepo expenseRepo;
    private final GroupRepo groupRepo;
    private final UserRepo userRepo;

    public record createExpenseRequest(
            @NotNull @Positive BigDecimal amount,
            @NotBlank String description,
            @NotNull @Positive Long PaidByid,
            @NotEmpty List<@NotNull @Positive Long> ParticipantsIds,
            @NotNull LocalDate date
    ) {}
    public record ExpenseResponse(Long id, String description, BigDecimal amount ,String paidBy ,List<String> Participants) {}

    @PostMapping("/groups/{groupId}/expenses")
    public ExpenseResponse add(@PathVariable Long groupId , @Valid @RequestBody createExpenseRequest req){
        ExpenseGroup group=groupRepo.findById(groupId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Group not found"));
        Set<Long>memberIds=group.getMembers().stream().map(user::getId).collect(Collectors.toSet());
        if(!memberIds.contains(req.PaidByid()) || !memberIds.containsAll(req.ParticipantsIds())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Members not found");
        }

        Expense expense=new Expense();
        expense.setAmount(req.amount().setScale(2, RoundingMode.HALF_UP));
        expense.setDescription(req.description());
        expense.setDate(req.date());
        expense.setPaidBy(userRepo.getReferenceById(req.PaidByid()));
        expense.setParticipants(userRepo.findAllById(req.ParticipantsIds()));
        expense.setExpenseGroup(group);
        expenseRepo.save(expense);
        return toResponse(expense);

    }
    

    @GetMapping("/groups/{groupId}/expenses")
    public List<ExpenseResponse>  list(@PathVariable Long groupId){
        return expenseRepo.findAllByExpenseGroup_Id(groupId).stream().map(this::toResponse).toList();
    }
    

    private ExpenseResponse toResponse(@NonNull Expense expense){
            return new ExpenseResponse(expense.getId(),expense.getDescription(),expense.getAmount(),expense.getPaidBy().getName(),expense
                    .getParticipants().stream().map(user::getName).toList());
    }
}
