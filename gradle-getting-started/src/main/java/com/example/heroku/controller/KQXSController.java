package com.example.heroku.controller;

import com.example.heroku.service.Parse2JsonService;
import com.example.heroku.dto.KQXSDto;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class KQXSController {
    @Autowired
    private Parse2JsonService parse2JsonService;

    @RequestMapping("/kqxs/json")
    public ResponseEntity<List<KQXSDto>> parseKQXS2Json() throws IOException, FeedException {
        String url = "https://xskt.com.vn/rss-feed/mien-bac-xsmb.rss";;
        URL feedUrl = new URL(url);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader((feedUrl)));

        // parse to json
        List<KQXSDto> kqxsDtos = new ArrayList<>();

        for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
            KQXSDto kqxsDto = new KQXSDto();
            kqxsDto.setTitle(entry.getTitle());
            kqxsDto.setDescription(parse2JsonService.string2KQXSDescription(entry.getDescription().getValue()));
            kqxsDto.setLink(entry.getLink());
            kqxsDto.setPublishedDate(entry.getPublishedDate());

            kqxsDtos.add(kqxsDto);
        }

        return new ResponseEntity<>(kqxsDtos, HttpStatus.OK);
    }
}