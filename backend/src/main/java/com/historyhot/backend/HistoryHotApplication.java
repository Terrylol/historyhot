package com.historyhot.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for History Hot Searches
 * Enables scheduling for periodic trending search data collection
 */
@SpringBootApplication
@EnableScheduling
public class HistoryHotApplication {

    public static void main(String[] args) {
        SpringApplication.run(HistoryHotApplication.class, args);
    }
} 