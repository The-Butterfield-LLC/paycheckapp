package com.butterfield.paycheckapp.controller;

import com.butterfield.paycheckapp.database.dao.PaycheckDAO;
import com.butterfield.paycheckapp.database.dao.PaycheckTransactionDAO;
import com.butterfield.paycheckapp.database.dao.TransactionDAO;
import com.butterfield.paycheckapp.database.entity.Paycheck;
import com.butterfield.paycheckapp.database.entity.PaycheckTransaction;
import com.butterfield.paycheckapp.database.entity.Transaction;
import com.butterfield.paycheckapp.formBean.TransactionFormBean;
import com.butterfield.paycheckapp.service.PaycheckTransactionService;
import com.butterfield.paycheckapp.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Slf4j
@Controller
public class TransactionController {
    @Autowired
    private TransactionDAO transactionDAO;

    @Autowired
    private PaycheckDAO paycheckDAO;

    @Autowired
    private PaycheckTransactionDAO paycheckTransactionDAO;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PaycheckTransactionService paycheckTransactionService;

    @RequestMapping(value="paycheck/{aID}/transaction/submit", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView submitTransaction(@Valid TransactionFormBean form, @PathVariable("aID") Integer id) throws Exception {
        ModelAndView response = new ModelAndView();
        Transaction transaction = null;

        if (transactionDAO.findById(form.getId()) == null) {
            //Create new transaction and new paycheck_transaction
            transaction = new Transaction();
            transaction = transactionService.saveTransaction(form, transaction);
            Paycheck paycheck = paycheckDAO.findById(id);
            PaycheckTransaction paycheckTransaction = paycheckTransactionService.savePaycheckTransaction(paycheck, transaction);

            // Saving to DB
            transactionDAO.save(transaction);
            paycheckTransactionDAO.save(paycheckTransaction);
        }
        else {
            //Update the transaction
            transaction = transactionDAO.findById(form.getId());
            transaction = transactionService.saveTransaction(form, transaction);
            transactionDAO.save(transaction);
        }
        response.setViewName("redirect:../../../paycheck/" + id);
        return response;
    }

    @RequestMapping(value="paycheck/{aID}/transaction/getTransaction/{tID}", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView getTransaction(@Valid TransactionFormBean form, @PathVariable("aID") Integer id, @PathVariable("tID") Integer tid) throws Exception {
        ModelAndView response = new ModelAndView();
        Transaction transaction = transactionDAO.findById(tid);
        log.debug(transaction.toString());
        response.addObject("specificTransaction", transaction);
        response.setViewName("redirect:../../../../paycheck/" + id);
        return response;
    }
}
