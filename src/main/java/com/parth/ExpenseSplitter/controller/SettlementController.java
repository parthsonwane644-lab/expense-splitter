package com.parth.ExpenseSplitter.controller;

import com.parth.ExpenseSplitter.Repo.GroupRepo;
import com.parth.ExpenseSplitter.Repo.SettlementRepo;
import com.parth.ExpenseSplitter.Repo.UserRepo;
import com.parth.ExpenseSplitter.model.ExpenseGroup;
import com.parth.ExpenseSplitter.model.Settlement;
import com.parth.ExpenseSplitter.model.user;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups/{groupId}/settlements")
public class SettlementController {
    private final SettlementRepo settlementRepo;
    private final GroupRepo groupRepo;
    private final UserRepo userRepo;

    public record CreateSettlementRequest(@NotNull long FromUserId,@NotNull long toUserId, @NotNull @Positive BigDecimal amount){}

    @PostMapping
    public Long create(@PathVariable long groupId, @Valid @RequestBody CreateSettlementRequest req){
        ExpenseGroup group=groupRepo.findById(groupId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Group not found"));
        if(req.FromUserId()==req.toUserId()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"cannot pay yourself");
        }
        Settlement s=new Settlement();
        s.setExpenseGroup(group);
        s.setFromUser(userRepo.getReferenceById(req.FromUserId()));
        s.setToUser(userRepo.getReferenceById(req.toUserId()));
        s.setAmount(req.amount().setScale(2, RoundingMode.HALF_UP));
        return settlementRepo.save(s).getId();
    }
}
