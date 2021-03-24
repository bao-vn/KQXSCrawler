package com.example.heroku.controller;

import com.example.heroku.service.CrawlerService;
import com.rometools.rome.io.FeedException;
import java.io.IOException;
import java.text.ParseException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class CrawlerController {
    @Autowired
    private CrawlerService crawlerService;

    @GetMapping("/save")
    public ResponseEntity<String> save() throws IOException, FeedException, ParseException {
        crawlerService.save();
        return new ResponseEntity<>("Hehe, save successful!!!", HttpStatus.OK);
    }
}
