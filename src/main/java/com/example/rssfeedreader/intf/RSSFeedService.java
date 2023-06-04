package com.example.rssfeedreader.intf;

import com.example.rssfeedreader.dtos.FeedItemInfo;
import com.example.rssfeedreader.dtos.ResultObject;
import com.example.rssfeedreader.entites.FeedItem;
import com.example.rssfeedreader.exceptionhandling.exceptions.RSSFeedException;
import java.util.List;

public interface RSSFeedService {
    List<FeedItemInfo> retrieveRssFeed(String url) throws RSSFeedException;
    void storeFeedItems( List<FeedItemInfo> itemInfos);
    List<FeedItemInfo> fetchLatestItems() throws RSSFeedException;
    ResultObject<FeedItem> fetchPaginatedItems(Integer limit, Integer offset, String sort, String direction) throws RSSFeedException;
}
