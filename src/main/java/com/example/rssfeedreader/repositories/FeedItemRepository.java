package com.example.rssfeedreader.repositories;

import com.example.rssfeedreader.entites.FeedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedItemRepository extends JpaRepository<FeedItem, Integer> {

}
