package com.rassus.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private int measurement;
    private Long scalarTime;
    private int[] vectorTime;
}
