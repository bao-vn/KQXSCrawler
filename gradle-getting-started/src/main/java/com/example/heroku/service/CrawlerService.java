package com.example.heroku.service;

import com.example.heroku.dto.KQXSDto;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlerService {
    @Autowired
    private Parse2JsonService parse2JsonService;

    /**
     * Get KQXS from rss link
     *
     * @param url path get from DB
     * @return List<KQXSDto> group by companyID
     * @throws IOException URL, XmlReader Exception
     * @throws FeedException
     */
    public List<KQXSDto> getKQXSFromRssLink(String url) throws IOException, FeedException {
        URL feedUrl = new URL(url);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader((feedUrl)));

        // parse to json
        List<KQXSDto> kqxsDtos = new ArrayList<>();

        for (SyndEntry entry : feed.getEntries()) {
            KQXSDto kqxsDto = KQXSDto.builder()
                    .title(entry.getTitle())
                    .description(parse2JsonService.multipleString2KQXSDescription(entry.getDescription().getValue()).get(0))
                    .link(entry.getLink())
                    .publishedDate(entry.getPublishedDate())
                    .build();

            kqxsDtos.add(kqxsDto);
        }

        return kqxsDtos;
    }
}
