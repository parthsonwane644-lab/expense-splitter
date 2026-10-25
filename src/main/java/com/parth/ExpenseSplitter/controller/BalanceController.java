package com.parth.ExpenseSplitter.controller;

import com.parth.ExpenseSplitter.component.CurrentUser;
import com.parth.ExpenseSplitter.service.BalanceService;
import com.parth.ExpenseSplitter.model.ExpenseGroup;
import com.parth.ExpenseSplitter.Repo.GroupRepo;
import com.parth.ExpenseSplitter.service.simplifyService;
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
    private final simplifyService s;
    private final BalanceService balanceService;
    private final GroupRepo groupRepo;
    private final CurrentUser currentUser;
    public record BalanceDto( Long userId,String name, BigDecimal balance) {
    }
    @GetMapping("/balances")
    public List<BalanceDto> balances(@PathVariable Long groupId){
        ExpenseGroup g=groupRepo.findById(groupId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"group not found"));
        Long me =currentUser.get().getId();
        if(g.getMembers().stream().noneMatch(m->m.getId().equals(me))){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"you are not the member of this group");
        }
        Map<Long,BigDecimal>net =balanceService.netBalances(groupId);
        return g.getMembers().stream()
                .map(u->new BalanceDto(u.getId(),u.getName(),net.getOrDefault(u.getId(),BigDecimal.ZERO)))
                .toList();
    }

    @GetMapping("/settle-plan")
    List<simplifyService.transfer> settlePlan(@PathVariable Long groupId){

        ExpenseGroup g= groupRepo.findById(groupId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"group not found"));
        Long me =currentUser.get().getId();
        if(g.getMembers().stream().noneMatch(m->m.getId().equals(me))){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"you are not the member of this group");
        }
        return s.simplify(balanceService.netBalances(groupId));
    }


}
