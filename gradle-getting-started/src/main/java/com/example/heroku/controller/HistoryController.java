package com.example.heroku.controller;

import com.example.heroku.service.HistoryService;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HistoryController {
    @Autowired
    private HistoryService historyService;


    /**
     * Get list of documentID
     *
     * @param colPath String
     * @return ResponseEntity<List<String>> list of document in collection
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/doc/{colPath}")
    public ResponseEntity<List<String>> getDocumentPaths(@PathVariable("colPath") String colPath) throws ExecutionException, InterruptedException {
        List<String> docPaths = historyService.getDocumentPaths(colPath);

        return new ResponseEntity<>(docPaths, HttpStatus.OK);
    }

    /**
     * Store all result collections by date when initiate database
     *
     * @param colPath String CollectionID
     * @return ResponseEntity<String>
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/doc/sync/{colPath}")
    public ResponseEntity<String> syncHistoryByCollectionID(@PathVariable("colPath") String colPath) throws ExecutionException, InterruptedException {
        historyService.syncHistoryByCollectionID(colPath);

        return new ResponseEntity<>("Hehe, successful", HttpStatus.OK);
    }

    @GetMapping("/doc/sync-all")
    public ResponseEntity<String> syncHistoryAllDB() throws ExecutionException, InterruptedException {
        historyService.syncHistoryAllDB();

        return new ResponseEntity<>("Hehe, successful", HttpStatus.OK);
    }
}
