create table recipe
(
    rid      int          not null
        primary key,
    mod_date datetime(6)  null,
    reg_date datetime(6)  not null,
    rclass   varchar(255) null,
    rcontent longtext     null,
    rgoodcnt int          null,
    rimg     varchar(255) null,
    rmenu    varchar(255) null,
    rselect  varchar(255) null,
    rtime    varchar(255) null,
    rviewcnt int          null,
    rwriter  varchar(20)  null,
    mid      int          null,
    constraint FKkaub65uraufw2xqt06lw6crug
        foreign key (mid) references member (mid)
);

