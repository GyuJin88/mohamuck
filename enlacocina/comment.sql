create table comment
(
    cid      int           not null
        primary key,
    mod_date datetime(6)   null,
    reg_date datetime(6)   not null,
    cbody    varchar(1000) null,
    cwriter  varchar(20)   null,
    recipeid int           null,
    constraint FK55c6amcy33grb3c6s10kq3d2a
        foreign key (recipeid) references recipe (rid)
);

