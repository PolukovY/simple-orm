package com.levik.demo;

import com.levik.demo.entity.Person;
import com.levik.orm.Session;
import com.levik.orm.SessionFactory;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

@Slf4j
public class SimpleOrmDemo {

    public static void main( String[] args ) {
        var dataSource = createDataSource();
        SessionFactory sessionFactory = new SessionFactory(dataSource);
        try (Session session = sessionFactory.openSession()) {
            Person person = session.findById(Person.class, 1);
            log.info("Person " + person.toString());
        }
    }

    public static DataSource createDataSource() {
        var dataSource = new PGSimpleDataSource();
        dataSource.setURL("jdbc:postgresql://localhost:5432/db");
        dataSource.setDatabaseName("db");
        dataSource.setUser("user");
        dataSource.setPassword("password");
        return dataSource;
    }
}
