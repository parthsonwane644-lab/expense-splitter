package com.parth.ExpenseSplitter.service;

import com.parth.ExpenseSplitter.Repo.SettlementRepo;
import com.parth.ExpenseSplitter.model.Expense;
import com.parth.ExpenseSplitter.Repo.ExpenseRepo;
import com.parth.ExpenseSplitter.model.Settlement;
import com.parth.ExpenseSplitter.model.SplitUtil;
import com.parth.ExpenseSplitter.model.user;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final ExpenseRepo expenseRepo;
    private final SettlementRepo settlementRepo;

    public Map<Long, BigDecimal> netBalances(long groupId) {
        Map<Long, BigDecimal> net = new HashMap<>();

        for (Expense e : expenseRepo.findAllByExpenseGroup_Id(groupId)) {
            net.merge(e.getPaidBy().getId(), e.getAmount(), BigDecimal::add);

            List<user> people = e.getParticipants();
            List<BigDecimal> shares = SplitUtil.splitEqually(e.getAmount(), people.size());
            for (int i = 0; i < people.size(); i++) {
                net.merge(people.get(i).getId(), shares.get(i).negate(), BigDecimal::add);
            }

        }
        for (Settlement s : settlementRepo.findByExpenseGroup_Id(groupId)) {
            net.merge(s.getFromUser().getId(), s.getAmount(), BigDecimal::add);
            net.merge(s.getToUser().getId(), s.getAmount(), BigDecimal::add);
        }

        return net;
    }
}
