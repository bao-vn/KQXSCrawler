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

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Repository
@Slf4j
public class FireBaseRepository {
    private FirebaseDatabase realtimeDB;
    private Firestore firestore;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private CommonUtils commonUtils;

    /**
     * Constructor
     *
     * @throws IOException
     */
    public FireBaseRepository() throws IOException {
        File file = ResourceUtils.getFile("classpath:firebase/key.json");
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
        crawlerDtos.setUpdatedTime(FieldValue.serverTimestamp());
        documentReference.set(crawlerDtos);
    }
}
