create sequence subscriber_id_seq start with 100 increment by 50;

create table subscribers
(
    id         bigint       not null default nextval('subscriber_id_seq'),
    email      varchar(300) not null unique,
    verified   boolean      not null default false,
    created_at timestamp    not null default current_timestamp,
    updated_at timestamp,
    primary key (id)
);
