create sequence setting_id_seq start with 100 increment by 50;

create table settings
(
    id                     bigint       not null default nextval('setting_id_seq'),
    admin_contact_name     varchar(250) not null,
    admin_contact_email    varchar(300) not null,
    admin_contact_address  varchar(300) not null,
    admin_contact_twitter  varchar(300),
    admin_contact_github   varchar(300),
    admin_contact_linkedin varchar(300),
    admin_contact_youtube  varchar(300),
    auto_approve_comment   boolean      not null default false,
    created_at             timestamp    not null default current_timestamp,
    updated_at             timestamp,
    primary key (id)
);

insert into settings(admin_contact_name, admin_contact_email, admin_contact_address,
                     admin_contact_twitter, admin_contact_github,
                     admin_contact_linkedin, admin_contact_youtube)
values ('K Siva Prasad Reddy', 'siva@sivalabs.in', 'Hyderabad, India',
        'https://x.com/sivalabs', 'https://github.com/sivaprasadreddy',
        'https://www.linkedin.com/in/ksivaprasadreddy/', 'https://www.youtube.com/sivalabs')
;