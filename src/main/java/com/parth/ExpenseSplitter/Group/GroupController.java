package com.parth.ExpenseSplitter.Group;

import com.parth.ExpenseSplitter.user.UserRepo;
import com.parth.ExpenseSplitter.user.user;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {
    private final GroupRepo groupRepo;
    private final UserRepo userRepo;

    public record CreateGroup(@NotBlank String name) {}
    public record AddMemberReq(@NotNull long id) {}
    public record MemberDto(long id, String name) {}
    public record GroupResponse(long id, String name, List<MemberDto> members) {}


    @PostMapping
    public GroupResponse create(@Valid @RequestBody CreateGroup req) {
        ExpenseGroup group = new ExpenseGroup();
        group.setName(req.name());
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

    private GroupResponse toResponse(ExpenseGroup group) {
        List<MemberDto> members = group.getMembers().stream()
                .map(user -> new MemberDto(user.getId(), user.getName()))
                .toList();

        return new GroupResponse(group.getId(), group.getName(), members);
    }
}

