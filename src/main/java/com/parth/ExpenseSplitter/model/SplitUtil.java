package com.parth.ExpenseSplitter.model;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.ArrayList;

public class SplitUtil {
    public static List<BigDecimal> splitEqually(BigDecimal total, int people) {
        BigDecimal n=BigDecimal.valueOf(people);
        BigDecimal base=total.divide(n,2, RoundingMode.DOWN);

        int extraCents=total.subtract(base.multiply(n)).movePointRight(2).intValue();
        List<BigDecimal> shares=new ArrayList<>();
        for(int i=0;i<people;i++){
            shares.add(i<extraCents? base.add(new BigDecimal("0.01")):base);
        }
    return  shares;
    }
}
