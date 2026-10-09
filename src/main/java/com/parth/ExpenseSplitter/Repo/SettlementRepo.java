package com.parth.ExpenseSplitter.Repo;

import com.parth.ExpenseSplitter.model.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRepo extends JpaRepository<Settlement, Long> {

    List<Settlement> findByExpenseGroup_Id(Long groupId);
}
