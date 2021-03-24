package com.example.heroku.controller;

import com.example.heroku.repository.FireBaseRepository;
import com.example.heroku.service.Parse2JsonService;
import com.example.heroku.dto.KQXSDto;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.text.SimpleDateFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api")
public class KQXSController {
    @Autowired
    private Parse2JsonService parse2JsonService;

    @Autowired
    private FireBaseRepository fireBaseService;

    @RequestMapping("/kqxs/mien-bac")
    public ResponseEntity<List<KQXSDto>> parseKQXS2Json() throws IOException, FeedException {
        String url = "https://xskt.com.vn/rss-feed/an-giang-xsag.rss";
        URL feedUrl = new URL(url);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader((feedUrl)));

        // parse to json
        List<KQXSDto> kqxsDtos = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        for (SyndEntry entry : feed.getEntries()) {
            KQXSDto kqxsDto = KQXSDto.builder()
                    .title(entry.getTitle())
                    .results(parse2JsonService.string2KQXSDescription(entry.getDescription().getValue()))
                    .link(entry.getLink())
                    .publishedDate(formatter.format(entry.getPublishedDate()))
                    .build();

            kqxsDtos.add(kqxsDto);
        }

        return new ResponseEntity<>(kqxsDtos, HttpStatus.OK);
    }

    @RequestMapping("/kqxs/mien-nam")
    public ResponseEntity<List<KQXSDto>> parseKQXSMienNam() throws IOException, FeedException {
        String url = "https://xskt.com.vn/rss-feed/mien-nam-xsmn.rss";
        URL feedUrl = new URL(url);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader((feedUrl)));

        // parse to json
        List<KQXSDto> kqxsDtos = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        for (SyndEntry entry : feed.getEntries()) {
            KQXSDto kqxsDto = KQXSDto.builder()
                    .title(entry.getTitle())
                    .results(parse2JsonService.multipleString2KQXSDescription(entry.getDescription().getValue()).get(0))
                    .link(entry.getLink())
                    .publishedDate(formatter.format(entry.getPublishedDate()))
                    .build();

            kqxsDtos.add(kqxsDto);
        }

        return new ResponseEntity<>(kqxsDtos, HttpStatus.OK);
    }

    @RequestMapping("/kqxs/data")
    public ResponseEntity<Map<String, Object>> readData() throws ExecutionException, InterruptedException {
        Firestore fireStore = fireBaseService.getFireStore();

        CollectionReference kqxsCrawler = fireStore.collection("KQXSCrawler");
        DocumentReference anGiang = kqxsCrawler.document("AnGiang");
        DocumentReference binhDinh = fireStore.document("KQXSCrawler/BinhDinh");
        // asynchronously retrieve the document
        ApiFuture<DocumentSnapshot> futureAnGiang = anGiang.get();
        ApiFuture<DocumentSnapshot> futureBinhDinh = binhDinh.get();
        // ...
        // future.get() blocks on response
        DocumentSnapshot documentAnGiang = futureAnGiang.get();
        DocumentSnapshot documentBinhDinh = futureBinhDinh.get();

        Map<String, Object> result = new HashMap<>();
        if (documentAnGiang.exists()) {
            result = documentAnGiang.getData();
            log.info("Document data: " + documentAnGiang.getData());
        } else {
            log.info("No such document!");
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}