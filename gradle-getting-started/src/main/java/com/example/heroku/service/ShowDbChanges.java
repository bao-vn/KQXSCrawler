package com.example.heroku.service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class ShowDbChanges implements Runnable {
    public void run() {
        System.out.println("Hello world!!!");
        FireBaseService fbs = null;
        try {
            fbs = new FireBaseService();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatabaseReference ref = fbs.getDb()
                .getReference("/");
        ref.addValueEventListener(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot) {
                Object document = dataSnapshot.getValue();
                System.out.println(document);
            }


            public void onCancelled(DatabaseError error) {
                System.out.print("Error: " + error.getMessage());
            }
        });


    }
}
