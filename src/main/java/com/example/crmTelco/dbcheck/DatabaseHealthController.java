package com.example.crmTelco.dbcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class DatabaseHealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health/db")
    public String checkDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2) ? "Database connection is OK ✅"
                    : "Database connection is NOT valid ❌";
        } catch (Exception e) {
            return "Database connection FAILED ❌: " + e.getMessage();
        }
    }
}

