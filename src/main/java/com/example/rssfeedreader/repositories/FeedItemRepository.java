package com.example.rssfeedreader.repositories;

import com.example.rssfeedreader.entites.FeedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedItemRepository extends JpaRepository<FeedItem, Integer> {
    @Query(value = "SELECT * FROM FEED_ITEM I ORDER BY I.CREATED_TIME DESC LIMIT 10", nativeQuery = true)
    List<FeedItem> findTop10ByOrderByCreatedTimeAtDesc();

}
