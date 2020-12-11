package com.rassus.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.atomic.AtomicIntegerArray;

@Data
@AllArgsConstructor
@Builder
public class Message {
    private String id;
    private String host;
    private Type type;
    private int port;
    private float measurement;
    private Long scalarTime;
    private int[] vectorTime;
}
