package com.example.heroku.service;

import com.example.heroku.repository.FireBaseRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Save results by date
 */
@Service
public class HistoryService {
    @Autowired
    private FireBaseRepository fireBaseRepository;

    @Autowired
    private CompanyService companyService;

    /**
     * Get list of documentID
     *
     * @param collectionPath String
     * @return List<String>
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<String> getDocumentPaths(String collectionPath) throws ExecutionException, InterruptedException {
        Firestore firestore = fireBaseRepository.getFireStore();
        List<String> documentPaths = new ArrayList<>();
        CollectionReference collectionReference = firestore.collection(collectionPath);

        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future = collectionReference.get();

        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            documentPaths.add(document.getId());
        }

        return documentPaths;
    }

    /**
     * Store all result collections by date when initiate database
     */
    public void syncHistoryByCollectionID(String collectionPath) throws ExecutionException, InterruptedException {
        List<String> docPathsByCollectionID = this.getDocumentPaths(collectionPath);
        for (String doc : docPathsByCollectionID) {
            this.saveHistoryDayByDay(doc, collectionPath);
        }
    }

    public void syncHistoryAllDB() throws ExecutionException, InterruptedException {
        // Get all companies
        List<String> companiesName = companyService.getCompanyPaths();

        for (String company : companiesName) {
            this.syncHistoryByCollectionID(company);
        }
    }

    /**
     * Store all result collections day by day
     */
    public void saveHistoryDayByDay(String strDate, String results) throws ExecutionException, InterruptedException {
        Firestore firestore = fireBaseRepository.getFireStore();
        CollectionReference tblHistory = firestore.collection("tblHistory");
        DocumentReference document = tblHistory.document(strDate);

        // set data
        Map<String, Object> history = new HashMap<>();

        history.put("date", strDate);
        history.put("updatedTime", FieldValue.serverTimestamp());
        ApiFuture<WriteResult> initialResult = tblHistory.document(strDate).set(history, SetOptions.merge());
        initialResult.get();

        // Atomically add a new region to the "regions" array field.
        ApiFuture<WriteResult> arrayUnion = document.update("companyName",
            FieldValue.arrayUnion(results));
        arrayUnion.get();
    }
}
