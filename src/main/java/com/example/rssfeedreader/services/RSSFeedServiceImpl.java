package com.example.rssfeedreader.services;

import com.example.rssfeedreader.dtos.FeedItemInfo;
import com.example.rssfeedreader.dtos.ResultObject;
import com.example.rssfeedreader.entites.FeedItem;
import com.example.rssfeedreader.exceptionhandling.exceptions.RSSFeedException;
import com.example.rssfeedreader.intf.RSSFeedService;
import com.example.rssfeedreader.repositories.FeedItemRepository;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class RSSFeedServiceImpl implements RSSFeedService {
    private static final Logger logger = LoggerFactory.getLogger(RSSFeedServiceImpl.class);

    @Autowired
    FeedItemRepository feedItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<FeedItemInfo> retrieveRssFeed(String url) throws RSSFeedException {
        SyndFeed feed;

        try {
            URL feedUrl = new URL(url);
            FeedFetcher feedFetcher = new HttpURLFeedFetcher();
            feed = feedFetcher.retrieveFeed(feedUrl);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving data. Error: ", e);
            String message = String.format("Error occurred while retrieving data from feed url %s", url);
            throw new RSSFeedException(message);
        }

        if (feed.getEntries().size() == 0) {
            String message = String.format("No rss feed items found from url %s", url);
            throw new RSSFeedException(message);
        }

        List<SyndEntry> entries = feed.getEntries();
        List<FeedItemInfo> feedItemInfoList = new ArrayList<>();

        //looping through entries list
        for (SyndEntry entry : entries) {
            FeedItemInfo feedItemInfo = new FeedItemInfo();
            String title = entry.getTitle() != null ? entry.getTitle() : "";
            String link = entry.getLink() != null ? entry.getLink() : "";
            String description = entry.getDescription() != null ? entry.getDescription().getValue() : "";
            Date publishedDate = entry.getPublishedDate() != null ? entry.getPublishedDate() : new Date(0);
            feedItemInfo.setTitle(title);
            feedItemInfo.setLink(link);
            feedItemInfo.setDescription(description);
            feedItemInfo.setPublicationDate(publishedDate);
            feedItemInfoList.add(feedItemInfo);
        }
        return feedItemInfoList;
    }

    public void storeFeedItems(List<FeedItemInfo> itemInfoList) {
        List<FeedItem> feedItems = feedItemRepository.findAll();
        logger.info("Retrieved feed item list size {}", feedItems.size());

        for (FeedItemInfo feedItemInfo : itemInfoList) {
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
                    logger.debug("Feed item {} successfully saved in the database", existingFeedItem);
                }

            } else {
                ModelMapper modelMapper = new ModelMapper();
                FeedItem feedItem = modelMapper.map(feedItemInfo, FeedItem.class);
                feedItem.setCreatedTime(LocalDateTime.now(ZoneId.of("GMT")));
                feedItem.setModifiedTime(LocalDateTime.now(ZoneId.of("GMT")));
                feedItemRepository.save(feedItem);
                logger.debug("Feed item {} successfully saved in the database", feedItem);
            }
        }
    }

    public List<FeedItemInfo> fetchLatestItems() throws RSSFeedException {
        List<FeedItemInfo> feedItemInfoList = new ArrayList<>();

        try {
            List<FeedItem> feedItems = feedItemRepository.findTop10ByOrderByCreatedTimeAtDesc();
            logger.info("Retrieved latest feed item list size {}", feedItems.size());

            if (feedItems.size() == 0) {
                throw new RSSFeedException("Latest feed items not found");
            }

            for (FeedItem feedItem : feedItems) {
                ModelMapper modelMapper = new ModelMapper();
                FeedItemInfo feedItemInfo = modelMapper.map(feedItem, FeedItemInfo.class);
                feedItemInfoList.add(feedItemInfo);
            }

        } catch (Exception e) {
            logger.error("Error occurred while retrieving latest feed items. Error: ", e);
            String message = String.format("Error occurred while retrieving latest feed items, exception %s", e.getMessage());
            throw new RSSFeedException(message);
        }

        return feedItemInfoList;
    }

    public ResultObject<FeedItem> fetchPaginatedItems(Integer limit, Integer offset, String sort, String direction) throws RSSFeedException {
        logger.info("Retrieve paginated rss feed items from database started");

        //Perform validation for inputs
        validateInput(limit, offset, sort, direction);
        Page<FeedItem> feedItemPage;

        try {
            PageRequest pageRequest;
            if (direction.equalsIgnoreCase("asc")) {
                pageRequest = PageRequest.of(offset, limit, Sort.by(sort).ascending());
            } else {
                pageRequest = PageRequest.of(offset, limit, Sort.by(sort));
            }
            feedItemPage = feedItemRepository.findAll(pageRequest);
            logger.info("Retrieved paginated feed items size {}", feedItemPage.getTotalElements());
        } catch (Exception e) {
            logger.error("Error occurred while fetching paginated feed items from database, Error: ", e);
            String message = String.format("Error occurred while fetching paginated feed items from database, exception %s", e.getMessage());
            throw new RSSFeedException(message);
        }

        if (feedItemPage.getContent().size() == 0) {
            String msg = String.format("Paginated feed items data not found with limit %s, offset %s, sort %s, direction %s", limit, offset, sort, direction);
            throw new RSSFeedException(msg);
        }

        return new ResultObject<FeedItem>(feedItemPage.getContent(), feedItemPage.getTotalElements());
    }

    private void validateInput(Integer limit, Integer offset, String sort, String direction) throws RSSFeedException {
        if (limit <= 0) {
            throw new RSSFeedException("Pagination attribute 'limit' must be greater than 0");
        }

        if (offset < 0) {
            throw new RSSFeedException("Pagination attribute 'offset' couldn't be negative");
        }

        String[] validSortByValues = {"id", "description", "link", "publicationDate", "createdTime", "modifiedTime"};
        if (!Arrays.asList(validSortByValues).contains(sort)) {
            String msg = String.format("Provided input %s for sort is invalid", sort);
            throw new RSSFeedException(msg);
        }

        String[] validDirections = {"asc", "desc"};
        if (!Arrays.asList(validDirections).contains(direction)) {
            String msg = String.format("Provided input %s for direction is invalid. Valid directions are asc/desc.", direction);
            throw new RSSFeedException(msg);
        }
    }
}
