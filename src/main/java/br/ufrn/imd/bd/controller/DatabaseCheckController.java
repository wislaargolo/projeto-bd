package br.ufrn.imd.bd.controller;

import br.ufrn.imd.bd.connection.DatabaseConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;

@RestController
public class DatabaseCheckController {

    @GetMapping("/check-database")
    public ResponseEntity<String> checkDatabaseConnection() {
        Connection connection = null;
        try {
            connection = DatabaseConfig.getConnection();
            if (connection.isValid(2)) {
                return ResponseEntity.ok("Database connection is OK");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection is NOT OK");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection failed: " + e.getMessage());
        } finally {
            DatabaseConfig.close(connection);
        }
    }
}
