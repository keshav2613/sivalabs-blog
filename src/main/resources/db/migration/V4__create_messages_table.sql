create sequence message_id_seq start with 100 increment by 50;

create table messages
(
    id         bigint       not null default nextval('message_id_seq'),
    name       varchar(250) not null,
    email      varchar(300) not null,
    subject    varchar(300) not null,
    content    text         not null,
    created_at timestamp    not null default current_timestamp,
    updated_at timestamp,
    primary key (id)
);
