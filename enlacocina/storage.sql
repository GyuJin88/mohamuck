create table storage
(
    sid      int          not null
        primary key,
    mod_date datetime(6)  null,
    reg_date datetime(6)  not null,
    sbuydate varchar(255) null,
    simg     varchar(255) null,
    singre   varchar(255) null,
    skeep    varchar(255) null,
    squan    int          null,
    syutong  varchar(255) null,
    mid      int          null,
    rid      int          null,
    userid   int          null,
    constraint FK7q4i25sdlfv6n3awnx4yjf80k
        foreign key (userid) references user (id),
    constraint FKkyc745jgqdhapjgabq2ry2iby
        foreign key (rid) references recipe (rid),
    constraint FKl8bl060aidas4mnckvt8enscw
        foreign key (mid) references member (mid)
);

