create table if not exists users
(
    id         bigint primary key auto_increment,
    username   varchar(255) not null,
    password   varchar(255) not null,
    role       varchar(60)  not null,
    status     varchar(50)  not null,
    created_at timestamp,
    updated_at timestamp
);

create table if not exists files
(
    id       bigint primary key auto_increment,
    name     varchar(255) not null,
    location varchar(500) not null,
    status   varchar(50)  not null
);

create table if not exists events
(
    id        bigint primary key auto_increment,
    user_id   bigint,
    file_id   bigint,
    status    varchar(50) not null,
    timestamp timestamp   not null default current_timestamp,
    foreign key (user_id) references users (id),
    foreign key (file_id) references files (id)
);