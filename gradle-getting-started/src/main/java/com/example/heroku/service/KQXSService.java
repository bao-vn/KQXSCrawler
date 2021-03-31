package com.example.heroku.service;

import com.example.heroku.common.CommonUtils;
import com.example.heroku.common.Constants;
import com.example.heroku.dto.CrawlerDto;
import com.example.heroku.dto.History;
import com.example.heroku.dto.SearchResultDto;
import com.example.heroku.repository.FireBaseRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class KQXSService {

    @Autowired
    private FireBaseRepository fireBaseRepository;

    @Autowired
    private CommonUtils commonUtils;

    /**
     * Get by no and date
     *
     * @param no String
     * @param strDate LocalDate
     * @return List<SearchResultDto>
     */
    public List<SearchResultDto> getByNoAndDate(String no, String strDate) throws ExecutionException, InterruptedException {
        Firestore firestore = fireBaseRepository.getFireStore();
        String docPath = "tblHistory/" + strDate;
        DocumentReference docHistory = firestore.document(docPath);
        List<SearchResultDto> results = new ArrayList<>();

        // over 30 days: now > date + 30
        if (commonUtils.isOutDate(strDate)) {
            SearchResultDto outDate = SearchResultDto.builder()
                    .status(Constants.SEARCH_STATUS.OUT_DATE.name())
                    .build();
            results.add(outDate);

            return results;
        }

        // search in DB
        ApiFuture<DocumentSnapshot> future = docHistory.get();
        DocumentSnapshot documentByDate = future.get();
        History history;

        if (documentByDate.exists()) {
            history = documentByDate.toObject(History.class);

            List<String> companies = history.getCompanyName();
            if (CollectionUtils.isEmpty(companies)) {
                for (String company : history.getCompanyName()) {
                    results.add(this.getByNoAndCompanyAndDate(no, company, strDate));
                }

                return results;
            }
        }

        // NOT_PUBLISHED
        SearchResultDto notPublished = SearchResultDto.builder()
                .status(Constants.SEARCH_STATUS.NOT_PUBLISHED.name())
                .build();
        results.add(notPublished);

        return results;
    }

    /**
     * Get by no and company name
     *
     * @param no String
     * @param company String
     * @return SearchResultDto
     */
    public SearchResultDto getByNoAndCompany(String no, String company) {
        return new SearchResultDto();
    }

    /**
     * Get by no, companyName, date
     *
     * @param no String
     * @param company String
     * @param strDate String
     * @return SearchResultDto
     */
    public SearchResultDto getByNoAndCompanyAndDate(String no, String company, String strDate) throws ExecutionException, InterruptedException {
        Firestore firestore = fireBaseRepository.getFireStore();
        String docPath = company + '\\' + strDate;
        DocumentReference docCompany = firestore.document(docPath);

        ApiFuture<DocumentSnapshot> future = docCompany.get();
        DocumentSnapshot result = future.get();

        if (result.exists()) {
            CrawlerDto resultByCompanyAndDate = result.toObject(CrawlerDto.class);
            return this.winPrize(resultByCompanyAndDate.getResults(), no);
        }

        return new SearchResultDto();
    }

    public SearchResultDto winPrize(List<String> results, String no) {
        String winPrize = "";

        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).contains(no)) {
                winPrize = String.valueOf(i);
            }
        }

        return SearchResultDto.builder()
                .winPrizeName(winPrize)
                .build();
    }
}
