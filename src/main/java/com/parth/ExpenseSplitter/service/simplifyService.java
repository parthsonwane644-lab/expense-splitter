package com.parth.ExpenseSplitter.service;


import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class simplifyService {

    public record transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
    }


    public List<transfer> simplify(Map<Long, BigDecimal> balances) {
        Map<Long, BigDecimal> net = new HashMap<>(balances);

        List<transfer> result = new ArrayList<>();
        if (net.isEmpty()) {
            return result;
        }
        while (true) {
            Long creditor = null;
            Long debitor = null;

            for (var e : net.entrySet()) {
                BigDecimal v = e.getValue();
                if (creditor == null || v.compareTo(net.get(creditor)) > 0) {
                    creditor = e.getKey();

                }
                if (debitor == null || v.compareTo(net.get(debitor)) < 0) {
                    debitor = e.getKey();
                }

            }
            BigDecimal credit = net.get(creditor);
            BigDecimal debt = net.get(debitor).negate();
            if (credit.signum() <= 0 || debt.signum() <= 0) break;

            BigDecimal amount = credit.min(debt);
            result.add(new transfer(debitor, creditor, amount));
            net.put(creditor, credit.subtract(amount));
            net.put(debitor, net.get(debitor).add(amount));


        }
        return result;
    }

}
