package com.example.rssfeedreader.dtos;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
@ApiModel(description = "Details about the rss feed items")
public class ItemInfo {
    @ApiModelProperty("Title of the feed item")
    String title;

    @ApiModelProperty("Description of the feed item")
    String description;

    @ApiModelProperty("Description of the feed item")
    String link;

    @ApiModelProperty("Publication date of the feed item")
    LocalDateTime publicationDate;
}
