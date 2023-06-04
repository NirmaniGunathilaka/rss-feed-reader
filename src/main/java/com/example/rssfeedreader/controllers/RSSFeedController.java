package com.example.rssfeedreader.controllers;

import com.example.rssfeedreader.dtos.FeedItemInfo;
import com.example.rssfeedreader.dtos.ResultObject;
import com.example.rssfeedreader.entites.FeedItem;
import com.example.rssfeedreader.exceptionhandling.exceptions.RSSFeedException;
import com.example.rssfeedreader.intf.RSSFeedService;
import com.example.rssfeedreader.scheduler.RSSFeedReaderScheduler;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;
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

    @GetMapping(path = "/sample-rss")
    public Channel rss() {
        Channel channel = new Channel();
        channel.setFeedType("rss_2.0");
        channel.setTitle("Java Feed");
        channel.setDescription("Different Articles on latest technology");
        channel.setLink("https://howtodoinjava.com");
        channel.setUri("https://howtodoinjava.com");
        channel.setGenerator("In House Programming");

        Image image = new Image();
        image.setUrl("https://howtodoinjava.com/wp-content/uploads/2015/05/howtodoinjava_logo-55696c1cv1_site_icon-32x32.png");
        image.setTitle("HowToDoInJava Feed");
        image.setHeight(32);
        image.setWidth(32);
        channel.setImage(image);

        Date postDate = new Date();
        channel.setPubDate(postDate);

        Item item1 = new Item();
        item1.setAuthor("Lokesh Gupta");
        item1.setLink("https://howtodoinjava.com/spring5/webmvc/spring-mvc-cors-configuration/");
        item1.setTitle("Spring Examples");
        item1.setUri("https://howtodoinjava.com/spring5/webmvc/spring-mvc-cors-configuration/");
        item1.setComments("https://howtodoinjava.com/spring5/webmvc/spring-mvc-cors-configuration/#respond");

        com.rometools.rome.feed.rss.Category category = new com.rometools.rome.feed.rss.Category();
        category.setValue("CORS");
        item1.setCategories(Collections.singletonList(category));

        Description descr1 = new Description();
        descr1.setValue(
                "CORS helps in serving web content from multiple domains into browsers who usually have the same-origin " +
                        "security policy. In this example, we will learn to enable CORS support in Spring MVC application " +
                        "at method and global level."
                        + "The post <a rel=\"nofollow\" href=\"https://howtodoinjava.com/spring5/webmvc/spring-mvc-cors-" +
                        "configuration/\">Spring CORS Configuration Examples</a> appeared first on <a rel=\"nofollow\" href=\"https://howtodoinjava.com\">HowToDoInJava</a>.");
        item1.setDescription(descr1);
        item1.setPubDate(postDate);

        //2nd item
        Item item2 = new Item();
        item2.setAuthor("Lokesh Gupta");
        item2.setLink("https://howtodoinjava.com/spring5/webmvc/spring-security/");
        item2.setTitle("Spring Examples 22222");
        item2.setUri("https://howtodoinjava.com/spring5/webmvc/spring-security/");
        item2.setComments("https://howtodoinjava.com/spring5/webmvc/spring-security/#respond");

        com.rometools.rome.feed.rss.Category category1 = new com.rometools.rome.feed.rss.Category();
        category.setValue("CORS");
        item2.setCategories(Collections.singletonList(category));

        Description descr2 = new Description();
        descr2.setValue(
                "CORS.");
        item2.setDescription(descr2);
        item2.setPubDate(postDate);
        List<Item> itemList = new ArrayList();
        itemList.add(item1);
        itemList.add(item2);
        channel.setItems(itemList);
        return channel;
    }

    @GetMapping("/")
    @ApiOperation(value = "Retrieve rss feed from url",
            notes = "Read and retrieve rss feed from public rss feed url.",
            response = Map.class)
    public ResponseEntity<Map<String, Object>> fetchRssFeeds() throws RSSFeedException {
        logger.info("Retrieve rss feed from url started");
        Map<String, Object> result = new HashMap<>();
        List<FeedItemInfo> feedItemInfos = rssFeedService.retrieveRssFeed("http://rss.cnn.com/rss/cnn_topstories.rss");
        result.put("feed item count",new Integer(feedItemInfos.size()));
        result.put("feed data", feedItemInfos);
        logger.info("Retrieve rss feed from url ends");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/job-scheduler")
    @ApiOperation(value = "Run job scheduler",
            notes = "Run rss feed reader job  scheduler.",
            response = Map.class)
    public ResponseEntity<String> runJobScheduler() throws RSSFeedException {
        logger.info("Job scheduler started");
        rssFeedReaderScheduler.feedReaderSchedulerJob();
        logger.info("Job scheduler ends");
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
            notes = "Read and retrieve paginated rss feed items from database. Please provide asc/desc for direction.",
            response = Map.class)
    public ResponseEntity<ResultObject<FeedItem>> fetchPaginatedItems(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction) throws RSSFeedException {
        return ResponseEntity.status(HttpStatus.OK).body(rssFeedService.fetchPaginatedItems(limit, offset, sort, direction));
    }
}
