package com.example.heroku.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
@AllArgsConstructor
public class FireBaseService {
    private FirebaseDatabase realtimeDB;
    private Firestore firestore;

    public FireBaseService() throws IOException {
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
}
