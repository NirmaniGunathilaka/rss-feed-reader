package com.example.rssfeedreader.controllers;

import com.example.rssfeedreader.dtos.ItemInfo;
import com.example.rssfeedreader.dtos.ResultObject;
import com.example.rssfeedreader.entites.FeedItem;
import com.example.rssfeedreader.exceptionhandling.exceptions.RSSFeedException;
import com.example.rssfeedreader.intf.RSSFeedService;
import com.example.rssfeedreader.scheduler.RSSFeedReaderScheduler;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/rss")
public class RSSFeedController {

    private static final Logger logger = LoggerFactory.getLogger(RSSFeedController.class);

    @Autowired
    RSSFeedReaderScheduler rssFeedReaderScheduler;

    @Autowired
    RSSFeedService rssFeedService;

    @GetMapping("/")
    @ApiOperation(value = "Retrieve rss feed from url",
            notes = "Read and retrieve rss feed from public rss feed url.",
            response = Map.class)
    public ResponseEntity<Map<String, Object>> fetchRssFeeds() throws RSSFeedException {
        logger.info("Retrieve rss feed from url started");
        Map<String, Object> result = new HashMap<>();
        List<ItemInfo> itemInfoList = rssFeedService.retrieveRssFeed("http://rss.cnn.com/rss/cnn_topstories.rss");
        result.put("feed item count", new Integer(itemInfoList.size()));
        result.put("feed data", itemInfoList);
        logger.info("Retrieve rss feed from url ends");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/job-scheduler")
    @ApiOperation(value = "Run job scheduler",
            notes = "Run rss feed reader job  scheduler.",
            response = Map.class)
    public ResponseEntity<String> runJobScheduler() throws RSSFeedException {
        rssFeedReaderScheduler.feedReaderSchedulerJob();
        return ResponseEntity.status(HttpStatus.OK).body("SUCCESS");
    }

    @GetMapping("/items")
    @ApiOperation(value = "Retrieve latest rss feed 10 items from database",
            notes = "Read and retrieve latest rss feed 10 items from database.",
            response = Map.class)
    public ResponseEntity<Map<String, Object>> fetchLatestItems() throws RSSFeedException {
        logger.info("Retrieve latest rss feed 10 items from database started");
        Map<String, Object> result = new HashMap<>();
        result.put("latest feed data", rssFeedService.fetchLatestItems());
        logger.info("Retrieve latest rss feed 10 items from database ends");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/items-pagination")
    @ApiOperation(value = "Retrieve paginated rss feed items from database",
            notes = "Read and retrieve paginated rss feed items from database. Please provide asc/desc for input \"direction\". " +
                    "You can provide any of the value from {\"id\", \"description\", \"link\", " +
                    "\"publicationDate\", \"createdTime\", \"modifiedTime\"} for input \"sort\".",
            response = Map.class)
    public ResponseEntity<ResultObject<FeedItem>> fetchPaginatedItems(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction) throws RSSFeedException {
        return ResponseEntity.status(HttpStatus.OK).body(rssFeedService.fetchPaginatedItems(limit, offset, sort, direction));
    }
}
