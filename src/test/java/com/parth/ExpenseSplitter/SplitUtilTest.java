package com.parth.ExpenseSplitter;


import com.parth.ExpenseSplitter.model.SplitUtil;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SplitUtilTest {
    @Test
    public void testSplit() {
    List<BigDecimal> shares= SplitUtil.splitEqually(new BigDecimal("100.00"),3);

        BigDecimal sum=shares.stream().reduce(BigDecimal.ZERO,BigDecimal::add);
        assertEquals(0,sum.compareTo(new BigDecimal("100.00")));
        assertEquals(0,shares.get(0).compareTo(new BigDecimal("33.34")));
    }

}
