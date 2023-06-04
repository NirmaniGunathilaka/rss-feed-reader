package com.example.rssfeedreader.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ResultObject<T> {
    List<T> list;
    long total;
}
