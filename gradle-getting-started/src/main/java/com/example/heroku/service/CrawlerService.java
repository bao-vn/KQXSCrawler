package com.example.heroku.service;

import com.example.heroku.common.CommonUtils;
import com.example.heroku.dto.KQXSDto;
import com.example.heroku.dto.XoSoKienThiet;
import com.example.heroku.model.Company;
import com.example.heroku.repository.FireBaseRepository;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.internal.FirebaseService;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service
public class CrawlerService {
    @Autowired
    private FireBaseRepository fireBaseRepository;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private CompanyService companyService;

    /**
     * Get KQXS from rss link
     *
     * @param url path get from DB
     * @return List<KQXSDto> group by companyID
     * @throws IOException URL, XmlReader Exception
     * @throws FeedException
     */
    public List<KQXSDto> getKQXSFromRssLink(String url) throws IOException, FeedException, ParseException {
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
                results = commonUtils.multipleString2KQXSDescription(resultsData).get(0);
            } else {
                results = commonUtils.string2KQXSDescription(resultsData);
            }

            // parse date from link
            // Example: https://xskt.com.vn/xsag/ngay-18-3-2021
            Date date = commonUtils.parseToLocalDateFromLink(entry.getLink());
            String strDate = commonUtils.parseToStringDateFromLink(entry.getLink());

            KQXSDto kqxsDto = KQXSDto.builder()
                    .title(entry.getTitle())
                    .results(results)
                    .link(entry.getLink())
                    .publishedDate(date)
                    .strPublishedDate(strDate)
                    .build();

            kqxsDtos.add(kqxsDto);
        }

        return kqxsDtos;
    }

    public void save(Company company) throws IOException, FeedException, ParseException {
        // format pathDocument = "tblBinhDinh/<yyyy-MM-dd>"
//        String url = "https://xskt.com.vn/rss-feed/binh-dinh-xsbdi.rss";
        List<KQXSDto> kqxsDtos = this.getKQXSFromRssLink(company.getLink());

        for (KQXSDto kqxsDto : kqxsDtos) {
            String pathDocument = company.getCompanyName()
                + '/'
                + kqxsDto.getStrPublishedDate();
            fireBaseRepository.saveResults(pathDocument, kqxsDto);
        }
    }

    /**
     * Crawl data from rss link in Companies table
     *
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws ParseException
     * @throws IOException
     * @throws FeedException
     */
    public void crawlDataFromRssLink() throws ExecutionException, InterruptedException, ParseException, IOException, FeedException {
        List<Company> companies = companyService.getCompanies();

        for (Company company : companies) {
            this.save(company);
        }
    }
}
