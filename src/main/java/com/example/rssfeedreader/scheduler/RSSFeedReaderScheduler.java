package com.example.rssfeedreader.scheduler;

import com.example.rssfeedreader.dtos.ItemInfo;
import com.example.rssfeedreader.exceptionhandling.exceptions.RSSFeedException;
import com.example.rssfeedreader.intf.RSSFeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableAutoConfiguration
public class RSSFeedReaderScheduler {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RSSFeedService rssFeedService;

    @Scheduled(cron = "${job.scheduler.cron}")
    public void feedReaderSchedulerJob() throws RSSFeedException {
        logger.info("Job scheduler started");
        try {
            List<ItemInfo> itemInfoList = rssFeedService.retrieveRssFeed("http://rss.cnn.com/rss/cnn_topstories.rss");
            rssFeedService.storeFeedItems(itemInfoList);
        } catch (Exception e) {
            logger.error("Error occurred in scheduler. Error: ", e);
            String message = String.format("Error occurred in scheduler, Exception : %s", e.getMessage());
            throw new RSSFeedException(message);
        }
        logger.info("Job scheduler ends");
    }
}
