package com.example.heroku.controller;

import com.example.heroku.model.Company;
import com.example.heroku.service.CompanyService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @RequestMapping("/company-path")
    public ResponseEntity<List<String>> getCompanyPaths() {
        return new ResponseEntity<>(companyService.getCompanyPaths(), HttpStatus.OK);
    }

    @RequestMapping("/company")
    public ResponseEntity<List<Company>> getCompanies() throws ExecutionException, InterruptedException {
        return new ResponseEntity<>(companyService.getCompanies(), HttpStatus.OK);
    }

    @RequestMapping("/company/save")
    public ResponseEntity<String> saveCompanies() throws ExecutionException, InterruptedException, IOException {
        companyService.saveCompanies();
        return new ResponseEntity<String>("hehe, successful!!!", HttpStatus.OK);
    }
}
