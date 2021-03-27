package com.example.heroku.service;

import com.example.heroku.model.Company;
import com.example.heroku.repository.FireBaseRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import java.io.IOException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class CompanyService {
    @Autowired
    private FireBaseRepository fireBaseRepository;

    @Autowired
    private CrawlerService crawlerService;

    private static final String KQXS_COLLECTION = "tblCompanies";

    /**
     * Get list of company from DB
     *
     * @return List<String>
     */
    public List<String> getCompanyPaths() {
        Firestore firestore = fireBaseRepository.getFireStore();

        CollectionReference kqxs = firestore.collection(KQXS_COLLECTION);

        Iterable<DocumentReference> documentReferences = kqxs.listDocuments();
        List<DocumentReference> docRefs = StreamSupport.stream(documentReferences.spliterator(), false)
                .collect(Collectors.toList());

        return docRefs.stream()
                .map(documentReference -> documentReference.getPath().split("/")[1])
                .collect(Collectors.toList());
    }

    /**
     * Get link of company
     *
     * @return List<Company> contain (companyId, link)
     * @throws ExecutionException firebase exception
     * @throws InterruptedException firebase exception
     */
    public List<Company> getCompanies() throws ExecutionException, InterruptedException {
        List<String> companyPaths = this.getCompanyPaths();
        List<Company> companies = new ArrayList<>();
        Firestore firestore = fireBaseRepository.getFireStore();

        for (String companyPath : companyPaths) {
            DocumentReference documentReference = firestore.document(companyPath);
            DocumentSnapshot docSnapShot = documentReference.get().get();

            if (docSnapShot.exists()) {
                Map<String, Object> data = docSnapShot.getData();

                if (data != null) {
                    Object objCompanyId = data.get("companyName");
                    Object objLink = data.get("link");

                    if (objCompanyId == null || objLink == null) {
                        continue;
                    }

                    String companyId = objCompanyId.toString().trim();
                    String link = objLink.toString().trim();
                    Company company = new Company();
                    company.setCompanyName(companyId);
                    company.setLink(link);

                    companies.add(company);
                }
            }
        }

        return companies;
    }

    public void saveCompany(String docPath, Company company) throws ExecutionException, InterruptedException {
        Firestore firestore = fireBaseRepository.getFireStore();
        CollectionReference colCompanies = firestore.collection("tblCompanies");
        DocumentReference documentReference = colCompanies.document(docPath);
        ApiFuture<WriteResult> initial = documentReference.set(company);
        initial.get();
        Map<String, Object> updatedTime = new HashMap<>();
        updatedTime.put("updatedTime", FieldValue.serverTimestamp());
        documentReference.set(updatedTime, SetOptions.merge());
    }

    public void saveCompanies() throws IOException, ExecutionException, InterruptedException {
        List<Company> companies = crawlerService.crawlRssLinks();

        for (Company company : companies) {
            this.saveCompany(company.getCompanyName(), company);
        }
    }
}
