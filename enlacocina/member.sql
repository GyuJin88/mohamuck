create table member
(
    mid      int          not null
        primary key,
    mod_date datetime(6)  null,
    reg_date datetime(6)  not null,
    mbirth   varchar(10)  null,
    memail   varchar(100) not null,
    mnick    varchar(100) null,
    mphone   varchar(20)  null,
    mpwd     varchar(100) null,
    role     varchar(255) null,
    constraint UK_2r6umf1c0uyfmvwp30vmb3gjr
        unique (memail),
    constraint UK_jw25oivhv1m6ngqy8rj92i6gy
        unique (mnick)
);

