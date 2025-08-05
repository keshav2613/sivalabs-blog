create sequence user_id_seq start with 100 increment by 50;

create table users
(
    id         bigint       not null default nextval('user_id_seq'),
    email      varchar(255) not null,
    password   varchar(255) not null,
    name       varchar(255) not null,
    role       varchar(20)  not null,
    bio        text,
    image      varchar(500) not null default '/images/authors/user.jpg',
    created_at timestamp    not null default current_timestamp,
    updated_at timestamp,
    primary key (id),
    constraint user_email_unique unique (email)
);

create sequence category_id_seq start with 100 increment by 50;

create table categories
(
    id         bigint       not null default nextval('category_id_seq'),
    label      varchar(250) not null,
    slug       varchar(300) not null,
    created_at timestamp    not null default current_timestamp,
    updated_at timestamp,
    primary key (id),
    constraint categories_label_unique unique (label),
    constraint categories_slug_unique unique (slug)
);

create sequence tag_id_seq start with 100 increment by 50;

create table tags
(
    id         bigint       not null default nextval('tag_id_seq'),
    label      varchar(250) not null,
    slug       varchar(300) not null,
    created_at timestamp    not null default current_timestamp,
    updated_at timestamp,
    primary key (id),
    constraint tags_label_unique unique (label),
    constraint tags_slug_unique unique (slug)
);

create sequence post_id_seq start with 100 increment by 50;

create table posts
(
    id          bigint       not null default nextval('post_id_seq'),
    title       varchar(250) not null,
    slug        varchar(300) not null,
    md_content  text         not null,
    content     text         not null,
    cover_image varchar(500),
    category_id bigint       not null references categories (id),
    created_by  bigint       not null references users (id),
    status      varchar(20)  not null,
    created_at  timestamp    not null default current_timestamp,
    updated_at  timestamp,
    updated_by  bigint references users (id),
    primary key (id),
    constraint posts_slug_unique unique (slug)
);

create table posts_tags
(
    post_id bigint not null references posts (id),
    tag_id  bigint not null references tags (id),
    primary key (post_id, tag_id)
);

create sequence comment_id_seq start with 100 increment by 50;

create table comments
(
    id         bigint       not null default nextval('comment_id_seq'),
    post_id    bigint       not null references posts (id),
    name       varchar(150) not null,
    email      varchar(150),
    content    text         not null,
    status     varchar(20)  not null,
    created_at timestamp    not null default current_timestamp,
    updated_at timestamp,
    primary key (id)
);
