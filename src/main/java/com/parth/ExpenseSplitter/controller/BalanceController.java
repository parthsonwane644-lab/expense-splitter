package com.parth.ExpenseSplitter.controller;

import com.parth.ExpenseSplitter.service.BalanceService;
import com.parth.ExpenseSplitter.model.ExpenseGroup;
import com.parth.ExpenseSplitter.Repo.GroupRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("api/groups/{groupId}")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;
    private final GroupRepo groupRepo;

    public record BalanceDto( Long userId,String name, BigDecimal balance) {
    }
    @GetMapping("/balances")
    public List<BalanceDto> balances(@PathVariable Long groupId){
        ExpenseGroup g=groupRepo.findById(groupId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"group not found"));
        Map<Long,BigDecimal>net =balanceService.netBalances(groupId);
        return g.getMembers().stream()
                .map(u->new BalanceDto(u.getId(),u.getName(),net.getOrDefault(u.getId(),BigDecimal.ZERO)))
                .toList();
    }


}
