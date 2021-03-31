package com.example.heroku.repository;

import com.example.heroku.common.CommonUtils;
import com.example.heroku.dto.CrawlerDto;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class FireBaseRepository {
    private FirebaseDatabase realtimeDB;
    private Firestore firestore;

    @Autowired
    private CommonUtils commonUtils;

    /**
     * Constructor
     *
     * @throws IOException
     */
    public FireBaseRepository() throws IOException {
        File file = new File("D:\\java\\key.json");

        FileInputStream serviceAccount = new FileInputStream(file);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://kqxs-firestore.firebaseio.com/")
                .build();

        FirebaseApp.initializeApp(options);

        realtimeDB = FirebaseDatabase.getInstance();
        firestore = FirestoreClient.getFirestore();
    }

    public FirebaseDatabase getRealtimeDB() {
        return realtimeDB;
    }

    public Firestore getFireStore() {
        return firestore;
    }

    /**
     * Add document
     *
     * @param pathDocument String document ID: "collection/document"
     * @param crawlerDtos KQXSDto data
     */
    public void saveResults(String pathDocument, CrawlerDto crawlerDtos) {
        DocumentReference documentReference = this.firestore.document(pathDocument);
        // convert to Map<String, Object>: KQXSDto, XoSoKienThiet

        Map<String, Object> kqxs = commonUtils.convertToMap(crawlerDtos);
        kqxs.put("updatedTime", FieldValue.serverTimestamp());
        documentReference.set(kqxs);
    }
}
