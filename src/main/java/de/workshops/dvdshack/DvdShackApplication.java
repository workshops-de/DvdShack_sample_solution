package de.workshops.dvdshack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootApplication
public class DvdShackApplication implements CommandLineRunner {

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(DvdShackApplication.class, args);
	}

	@Override
	public void run(String... args) throws SQLException {
		System.out.println(
				dataSource.getConnection().getMetaData().getURL()
		);
	}
}
