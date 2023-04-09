package com.butterfield.paycheckapp.controller;

import com.butterfield.paycheckapp.database.dao.PaycheckDAO;
import com.butterfield.paycheckapp.database.dao.PaycheckTransactionDAO;
import com.butterfield.paycheckapp.database.dao.TransactionDAO;
import com.butterfield.paycheckapp.database.entity.Paycheck;
import com.butterfield.paycheckapp.database.entity.PaycheckTransaction;
import com.butterfield.paycheckapp.database.entity.Transaction;
import com.butterfield.paycheckapp.formBean.PaycheckFormBean;
import com.butterfield.paycheckapp.service.PaycheckService;
import com.butterfield.paycheckapp.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class PaycheckController {

    @Autowired
    private PaycheckDAO paycheckDAO;

    @Autowired
    private TransactionDAO transactionDAO;

    @Autowired
    private PaycheckTransactionDAO paycheckTransactionDAO;

    @Autowired
    private PaycheckService paycheckService;

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value="paycheck/paycheck", method = RequestMethod.GET)
    public ModelAndView emptyPaycheckPage() throws Exception {
        ModelAndView response = new ModelAndView();
        response.setViewName("paycheckInfo");
        return response;
    }

    @RequestMapping(value="paycheck/list", method = RequestMethod.GET)
    public ModelAndView listPaychecks() throws Exception {
        ModelAndView response = new ModelAndView();

        List<Paycheck> paychecks = paycheckDAO.findAll();
//        System.out.print(paychecks.stream().toList());
        response.addObject("paycheckList", paychecks);

        response.setViewName("paycheck/paycheckList");
        return response;
    }


    @RequestMapping(value="paycheck/saveCheck", method = RequestMethod.POST)
    public ModelAndView saveCheck(@Valid PaycheckFormBean form) throws Exception {
        ModelAndView response = new ModelAndView();

        Paycheck paycheck = null;

        if (form.getId() == null) {
            paycheck = new Paycheck();
            paycheck = paycheckService.saveCheck(form, paycheck);
            response.setViewName("redirect:../paycheck/list");
        }
        else{
            paycheck = paycheckDAO.findById(form.getId());
            paycheck = paycheckService.saveCheck(form, paycheck);
            response.setViewName("redirect:../paycheck/"+ paycheck.getId());
        }
        paycheckDAO.save(paycheck);

        return response;
    }

    @RequestMapping(value = "/paycheck/{aID}", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView indexWithPaycheck(@PathVariable("aID") Integer id) throws Exception {
        ModelAndView response = new ModelAndView();

        Paycheck paycheck = new Paycheck();

        if (paycheckDAO.findById(id) == null) {
            log.debug("No check!");
        }else{
            paycheck = paycheckDAO.findById(id);
            List<PaycheckTransaction> paycheckTransactionList =  paycheckTransactionDAO.findAllByPaycheckId(paycheck);
            List<Transaction> tList = paycheckTransactionList.stream().map(p -> p.getTransactionId()).collect(Collectors.toList());

            // Getting the total amount for Expense and Income for page display
            Float expenseTotalAmount = 0F;
            Float incomeTotalAmount = 0F;
            expenseTotalAmount = transactionService.getExpenseTotal(tList);
            incomeTotalAmount = transactionService.getIncomeTotal(tList) + paycheck.getPaycheckAmount();

            response.addObject("transactionList", paycheckTransactionList);
            response.addObject("incomeTotalAmount", incomeTotalAmount);
            response.addObject("expenseTotalAmount", expenseTotalAmount);
        }


        response.addObject("paycheck", paycheck);
        response.setViewName("paycheck/paycheckInfo");
        return response;
    }

}
