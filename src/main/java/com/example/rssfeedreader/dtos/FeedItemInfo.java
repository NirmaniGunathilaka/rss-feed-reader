package com.example.rssfeedreader.dtos;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
@ApiModel(description = "Details about the rss feed items")
public class FeedItemInfo {
    @ApiModelProperty("Title of the feed item")
    String title;

    @ApiModelProperty("Description of the feed item")
    String description;

    @ApiModelProperty("Description of the feed item")
    String link;

    @ApiModelProperty("Publication date of the feed item")
    Date publicationDate;
}
