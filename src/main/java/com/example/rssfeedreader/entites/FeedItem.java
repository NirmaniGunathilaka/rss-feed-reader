package com.example.rssfeedreader.entites;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="FEED_ITEM")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class FeedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    int id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION", length = 1024)
    private String description;

    @Column(name = "LINK")
    private String link;

    @Column(name = "PUBLICATION_DATE")
    private Date publicationDate;

    @Column(name = "CREATED_TIME")
    private LocalDateTime createdTime;

    @Column(name = "MODIFIED_TIME")
    private LocalDateTime modifiedTime;

}
