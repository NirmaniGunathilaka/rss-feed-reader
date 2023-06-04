package com.example.rssfeedreader.intf;

import com.example.rssfeedreader.dtos.FeedItemInfo;
import com.example.rssfeedreader.dtos.ResultObject;
import com.example.rssfeedreader.entites.FeedItem;
import com.example.rssfeedreader.exceptions.RSSException;
import java.util.List;

public interface RSSFeedService {
    List<FeedItemInfo> retrieveRssFeed(String url) throws RSSException;
    void storeFeedItems( List<FeedItemInfo> itemInfos);
    List<FeedItemInfo> fetchLatestItems();
    ResultObject<FeedItem> fetchPaginatedItems(Integer limit, Integer offset, String sort, String direction) throws RSSException;

}
