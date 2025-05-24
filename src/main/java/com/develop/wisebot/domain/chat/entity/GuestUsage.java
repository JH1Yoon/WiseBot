package com.develop.wisebot.domain.chat.entity;

import java.time.LocalDate;

public class GuestUsage {
    private int count;
    private LocalDate lastUsedDate;

    public GuestUsage() {
        this.count = 0;
        this.lastUsedDate = LocalDate.now();
    }

    public int getCount() {
        return count;
    }

    public void increment() {
        this.count++;
    }

    public LocalDate getLastUsedDate() {
        return lastUsedDate;
    }

    public void reset() {
        this.count = 0;
        this.lastUsedDate = LocalDate.now();
    }
}