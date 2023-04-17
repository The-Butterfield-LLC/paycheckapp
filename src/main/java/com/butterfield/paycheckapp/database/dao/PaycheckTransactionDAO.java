package com.butterfield.paycheckapp.database.dao;

import com.butterfield.paycheckapp.database.entity.Paycheck;
import com.butterfield.paycheckapp.database.entity.PaycheckTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaycheckTransactionDAO extends JpaRepository<PaycheckTransaction, Long> {

    List<PaycheckTransaction> findAllByPaycheckId(@Param("paycheckId") Paycheck paycheck);
}
