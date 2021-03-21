package com.example.heroku.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FireBaseService {
    FirebaseDatabase db;

    public FireBaseService() throws IOException {
//        File file = new File(
//                getClass().getClassLoader().getResource("key.json").getFile()
//        );

        File file = new File("D:\\java\\key.json");

        FileInputStream serviceAccount = new FileInputStream(file);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://kqxs-firestore.firebaseio.com/")
                .build();

        FirebaseApp.initializeApp(options);

        db = FirebaseDatabase.getInstance();
    }

    public FirebaseDatabase getDb() {
        return db;
    }
}
