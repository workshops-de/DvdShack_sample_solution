package de.workshops.dvdshack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootApplication
@Slf4j
public class DvdShackApplication implements CommandLineRunner {

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(DvdShackApplication.class, args);
	}

	@Override
	public void run(String... args) throws SQLException {
		try (var connection = dataSource.getConnection()) {
			log.info("Connection established: " + connection.getMetaData().getURL());
		}
	}
}
