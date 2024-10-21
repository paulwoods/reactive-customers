drop table customer if exists;

create table customer
(
    id         long generated always as identity primary key,
    first_name varchar(100) not null,
    last_name  varchar(100) not null
);
