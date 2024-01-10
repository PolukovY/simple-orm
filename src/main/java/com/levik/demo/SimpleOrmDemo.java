package com.levik.demo;

import com.levik.demo.entity.Person;
import com.levik.orm.BringSession;
import com.levik.orm.SessionFactory;
import com.levik.pool.CustomConnectionDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

@Slf4j
public class SimpleOrmDemo {

    @SneakyThrows
    public static void main( String[] args ) {
        var dataSource = createDataSource();
        SessionFactory sessionFactory = new SessionFactory(dataSource);
        try (BringSession session = sessionFactory.openSession()) {
            Person person = session.findById(Person.class, 1L);
            Person person2 = session.findById(Person.class, 1);
            log.info("Person {}",  person == person2);
        }
    }

    public static DataSource createDataSource() {
        return new CustomConnectionDataSource(
                "jdbc:postgresql://localhost:5432/db",  "user", "password"
        );
    }
}
