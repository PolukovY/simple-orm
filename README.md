### Simple ORM

## Getting started

- Navigate to the Docker folder and execute the following command to set up PostgreSQL with default settings:

```
docker-compose up -d
```

Here are SQL commands to create the persons table, populate it with initial data, and retrieve all records:

```sql
drop table persons;

create table persons (
    id bigserial primary key,
    first_name varchar(255),
    last_name varchar(255)
);

select * from persons;

insert into persons(first_name, last_name) values ('FirstName', 'LastName');

```

- The connection details are hardcoded in the SimpleOrmDemo class. 
To run a demonstration, execute the SimpleOrmDemo class using the following command:

```java
com.levik.demo.SimpleOrmDemo
```

- Demo Result:
  
<img width="951" alt="image" src="https://github.com/BlyznytsiaOrg/bring/assets/73576438/750a3ba5-fbe6-4339-adc0-e9d14a881745">

