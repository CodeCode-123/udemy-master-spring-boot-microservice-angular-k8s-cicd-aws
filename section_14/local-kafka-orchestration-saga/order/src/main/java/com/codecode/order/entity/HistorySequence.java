package com.codecode.order.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "history_sequence")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistorySequence {
    @Id
    private String id;
    private int sequence;
}
