create table material
(
    maid     int          not null
        primary key,
    mod_date datetime(6)  null,
    reg_date datetime(6)  not null,
    mama     varchar(255) null
);

