create table user
(
    id         int          not null
        primary key,
    address    varchar(200) null,
    email      varchar(100) not null,
    nickname   varchar(255) null,
    oauth_type varchar(50)  null,
    password   varchar(255) null,
    role       varchar(255) null,
    tel        varchar(30)  null,
    constraint UK_n4swgcf30j6bmtb4l4cjryuym
        unique (nickname),
    constraint UK_ob8kqyqqgmefl0aco34akdtpe
        unique (email)
);

