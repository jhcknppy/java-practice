package com.javapractice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SettleResult {
    private boolean completed;
    private boolean leveledUp;
    private int level;
    private int correct;
    private int total;
}
