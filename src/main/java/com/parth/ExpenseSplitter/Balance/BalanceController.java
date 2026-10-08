package com.parth.ExpenseSplitter.Balance;

import com.parth.ExpenseSplitter.Expense.ExpenseRepo;
import com.parth.ExpenseSplitter.Group.ExpenseGroup;
import com.parth.ExpenseSplitter.Group.GroupRepo;
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
