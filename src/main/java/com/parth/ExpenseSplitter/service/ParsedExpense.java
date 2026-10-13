package com.parth.ExpenseSplitter.service;

import java.math.BigDecimal;
import java.util.List;

public record ParsedExpense(String description, BigDecimal amount, String paidByName, List<String> participantNames) { }
