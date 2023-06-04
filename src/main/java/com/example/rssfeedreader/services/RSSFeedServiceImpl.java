package com.example.rssfeedreader.services;

import com.example.rssfeedreader.dtos.FeedItemInfo;
import com.example.rssfeedreader.entites.FeedItem;
import com.example.rssfeedreader.exceptions.RSSException;
import com.example.rssfeedreader.intf.RSSFeedService;
import com.example.rssfeedreader.repositories.FeedItemRepository;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RSSFeedServiceImpl implements RSSFeedService {

    private Logger logger = LogManager.getLogger(RSSFeedServiceImpl.class);

    @Autowired
    FeedItemRepository feedItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<FeedItemInfo> retrieveRssFeed(String url) throws RSSException {
        SyndFeed feed = null;
        try{
            URL feedUrl = new URL(url);
            FeedFetcher feedFetcher = new HttpURLFeedFetcher();
            feed = feedFetcher.retrieveFeed(feedUrl);
        }catch (Exception e){
             throw new RSSException("Error while retrieving data from feed url");
        }
        List<SyndEntry> entries = feed.getEntries();
        List<FeedItemInfo> feedItemInfos = new ArrayList<>();
        for(SyndEntry entry:entries){
            FeedItemInfo feedItemInfo = new FeedItemInfo();
            String title = entry.getTitle() != null? entry.getTitle():"";
            String link = entry.getLink() != null? entry.getLink():"";
            SyndContent syndContentDesc = entry.getDescription();
            String description = entry.getDescription() != null? entry.getDescription().getValue():"";
            Date publishedDate = entry.getPublishedDate() != null? entry.getPublishedDate():new Date(0);
            feedItemInfo.setTitle(title);
            feedItemInfo.setLink(link);
            feedItemInfo.setDescription(description);
            feedItemInfo.setPublicationDate(publishedDate);
            feedItemInfos.add(feedItemInfo);
        }
        return feedItemInfos;
    }

    public void storeFeedItems(List<FeedItemInfo> itemInfos) {

        List<FeedItem> feedItems = feedItemRepository.findAll();
        for (FeedItemInfo feedItemInfo : itemInfos) {
            FeedItem existingFeedItem = feedItems.stream().filter(item -> item.getLink().equals(feedItemInfo.getLink()))
                    .findAny()
                    .orElse(null);
            if (existingFeedItem != null) {
                boolean hasUpdate = false;
                if (!feedItemInfo.getTitle().equals(existingFeedItem.getTitle())) {
                    existingFeedItem.setTitle(feedItemInfo.getTitle());
                    hasUpdate = true;
                }

                if (!feedItemInfo.getDescription().equals(existingFeedItem.getDescription())) {
                    existingFeedItem.setDescription(feedItemInfo.getDescription());
                    hasUpdate = true;
                }

                if (!feedItemInfo.getLink().equals(existingFeedItem.getLink())) {
                    existingFeedItem.setLink(feedItemInfo.getLink());
                    hasUpdate = true;
                }

                if (!feedItemInfo.getPublicationDate().equals(existingFeedItem.getPublicationDate())) {
                    existingFeedItem.setPublicationDate(feedItemInfo.getPublicationDate());
                    hasUpdate = true;
                }

                if (hasUpdate) {
                    existingFeedItem.setModifiedTime(LocalDateTime.now(ZoneId.of("GMT")));
                    feedItemRepository.save(existingFeedItem);
                }
            } else {
                ModelMapper modelMapper = new ModelMapper();
                FeedItem feedItem = modelMapper.map(feedItemInfo, FeedItem.class);
//
//                FeedItem feedItem = new FeedItem();
//                feedItem.setTitle(feedItemInfo.getTitle());
//                feedItem.setDescription(feedItemInfo.getDescription());
//                feedItem.setLink(feedItemInfo.getLink());
//                feedItem.setPublicationDate(feedItemInfo.getPublicationDate());
                feedItem.setCreatedTime(LocalDateTime.now(ZoneId.of("GMT")));
                feedItem.setModifiedTime(LocalDateTime.now(ZoneId.of("GMT")));
                feedItemRepository.save(feedItem);
            }
        }
    }
    }
