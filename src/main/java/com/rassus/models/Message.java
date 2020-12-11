package com.rassus.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Message {
    private String id;
    private String host;
    private int port;
    private int measurement;
    private Long scalarTime;
    private int[] vectorTime;
}
