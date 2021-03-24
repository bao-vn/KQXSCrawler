package com.example.heroku.service;

import com.example.heroku.dto.KQXSDto;
import com.example.heroku.dto.XoSoKienThiet;
import com.example.heroku.repository.FireBaseRepository;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.internal.FirebaseService;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlerService {
    @Autowired
    private Parse2JsonService parse2JsonService;

    @Autowired
    private FireBaseRepository fireBaseRepository;

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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        for (SyndEntry entry : feed.getEntries()) {
            String resultsData = entry.getDescription().getValue();
            XoSoKienThiet results;
            if (resultsData.contains("\\[")) {
                results = parse2JsonService.multipleString2KQXSDescription(resultsData).get(0);
            } else {
                results = parse2JsonService.string2KQXSDescription(resultsData);
            }

            KQXSDto kqxsDto = KQXSDto.builder()
                    .title(entry.getTitle())
                    .results(results)
                    .link(entry.getLink())
                    .publishedDate(formatter.format(entry.getPublishedDate()))
                    .build();

            kqxsDtos.add(kqxsDto);
        }

        return kqxsDtos;
    }

    public void save() throws IOException, FeedException {
        // format pathDocument = "tblBinhDinh/<yyyy-MM-dd>"
        StringBuilder pathDocument = new StringBuilder("tblBinhDinh");
        String url = "https://xskt.com.vn/rss-feed/binh-dinh-xsbdi.rss";
        List<KQXSDto> kqxsDtos = this.getKQXSFromRssLink(url);

        for (KQXSDto kqxsDto : kqxsDtos) {
            pathDocument.append('/');
            pathDocument.append(kqxsDto.getPublishedDate());
            fireBaseRepository.saveResults(pathDocument.toString(), kqxsDtos.get(0));
        }

    }
}
