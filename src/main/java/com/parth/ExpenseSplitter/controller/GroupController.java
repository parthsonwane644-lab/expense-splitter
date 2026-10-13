package com.parth.ExpenseSplitter.controller;

import com.parth.ExpenseSplitter.Repo.ExpenseRepo;
import com.parth.ExpenseSplitter.Repo.GroupRepo;
import com.parth.ExpenseSplitter.Repo.SettlementRepo;
import com.parth.ExpenseSplitter.Repo.UserRepo;
import com.parth.ExpenseSplitter.component.CurrentUser;
import com.parth.ExpenseSplitter.model.ExpenseGroup;
import com.parth.ExpenseSplitter.model.user;
import com.parth.ExpenseSplitter.service.BalanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {
    private final GroupRepo groupRepo;
    private final UserRepo userRepo;
    private final CurrentUser currentUser;
    private final BalanceService balanceService;
    private final ExpenseRepo expenseRepo;
    private final SettlementRepo settlementRepo;

    public record CreateGroup(@NotBlank String name) {}
    public record AddMemberReq(@NotNull long id) {}
    public record MemberDto(long id, String name) {}
    public record GroupResponse(long id, String name,long createdById, List<MemberDto> members) {}


    @PostMapping
    public GroupResponse create(@Valid @RequestBody CreateGroup req) {
        ExpenseGroup group = new ExpenseGroup();

        group.setName(req.name());
        user me =currentUser.get();
        group.setCreatedBy(me);
        group.getMembers().add(currentUser.get());

        return toResponse(groupRepo.save(group));
    }

    @PostMapping("/{groupId}/members")
    public GroupResponse addMember(
            @PathVariable("groupId") long groupId,
            @Valid @RequestBody AddMemberReq req
    ) {
        ExpenseGroup group = groupRepo.findById(groupId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        user user = userRepo.findById(req.id())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!group.getMembers().contains(user)) {
            group.getMembers().add(user);
        }

        return toResponse(groupRepo.save(group));
    }


    @GetMapping("/{groupId}")
    public GroupResponse findById(@PathVariable("groupId") long groupId) {
        ExpenseGroup group = groupRepo.findById(groupId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        return toResponse(group);
    }
    public ExpenseGroup getGroupForMember(Long groupId) {
        ExpenseGroup g = groupRepo.findById(groupId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        Long me = currentUser.get().getId();
        if (g.getMembers().stream().noneMatch(u -> u.getId().equals(me))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not in this group");
        }
        return g;
    }

    @DeleteMapping("/{groupId}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long groupId) {
        ExpenseGroup g=getGroupForMember(groupId);
        if(!g.getCreatedBy().getId().equals(currentUser.get().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the group creator can delete this group");
        }
        settlementRepo.deleteAll(settlementRepo.findByExpenseGroup_Id(groupId));
        expenseRepo.deleteAll(expenseRepo.findAllByExpenseGroup_Id(groupId));
        groupRepo.delete(g);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable Long groupId, @PathVariable Long userId) {
        ExpenseGroup g=getGroupForMember(groupId);
        Long me = currentUser.get().getId();
        Long creatorId = g.getCreatedBy().getId();

        if(!me.equals(creatorId) && !me.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the creator can remove other members");
        }
        if(userId.equals(creatorId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Creator cannot be removed . delete the group instead");
        }

        BigDecimal balance=balanceService.netBalances(groupId).getOrDefault(userId, BigDecimal.ZERO);
        if(balance.signum()!=0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Settle up first. This member's balance is "+balance);
        }
        boolean removed=g.getMembers().removeIf(member -> member.getId().equals(userId));
        if(!removed){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "member not in this group");
        }
        groupRepo.save(g);
    }


    private GroupResponse toResponse(ExpenseGroup group) {
        List<MemberDto> members = group.getMembers().stream()
                .map(user -> new MemberDto(user.getId(), user.getName()))
                .toList();

        return new GroupResponse(group.getId(), group.getName(),group.getCreatedBy().getId(), members);
    }
}

