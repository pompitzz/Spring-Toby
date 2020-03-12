create table users(
    id varchar(10) primary key,
    name varchar(20) not null ,
    password varchar(10) not null,
    Level tinyint not null,
    Login int not null,
    Recommend int not null
);

truncate users;
alter table users
    add Level     tinyint not null,
    add Login     int     not null,
    add Recommend int     Not null;