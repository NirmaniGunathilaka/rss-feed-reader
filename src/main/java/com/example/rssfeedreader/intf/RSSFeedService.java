package com.example.rssfeedreader.intf;

import com.example.rssfeedreader.dtos.FeedItemInfo;
import com.example.rssfeedreader.exceptions.RSSException;

import java.util.List;

public interface RSSFeedService {
    List<FeedItemInfo> retrieveRssFeed(String url) throws RSSException;
    void storeFeedItems( List<FeedItemInfo> itemInfos);
}
