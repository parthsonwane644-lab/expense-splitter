package com.parth.ExpenseSplitter;


import com.parth.ExpenseSplitter.service.simplifyService;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

public class SimplifyServiceTest {
    @Test
    public void testSimplifyService(){
        Map<Long, BigDecimal> net = Map.of(1L, new BigDecimal("500"),
                2L, new BigDecimal("-100"),
                3L, new BigDecimal("-400"));
        List<simplifyService.transfer> plan = new simplifyService().simplify(net);
        assertEquals(2,plan.size());
        assertTrue(plan.stream().allMatch(t -> t.toUserId().equals(1L)));
    }
}
