package com.matheusmaciel.comissio.infra.config;

import org.flywaydb.core.Flyway;

public class FlywayMigrationRunner {
       public static void main(String[] args) {
    
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:postgresql://localhost:5433/comissio_db", "postgres", "postgres")
                .load();

        
        //flyway.migrate();

        
         flyway.repair();
    }
}
