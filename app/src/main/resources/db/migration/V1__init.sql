create table users (
                       id varchar(36) primary key,
                       email varchar(255) not null unique,
                       username varchar(100),
                       display_name varchar(255) not null,
                       password_hash varchar(255) not null,
                       created_at timestamp not null,
                       updated_at timestamp
);

create index idx_username on users (username);

create table channels (
                          id varchar(36) primary key,
                          name varchar(100) not null unique,
                          description varchar(500),
                          created_by varchar(36) not null,
                          created_at timestamp not null
);

create table channel_members (
                                 id varchar(36) primary key,
                                 channel_id varchar(36) not null,
                                 user_id varchar(36) not null,
                                 joined_at timestamp not null,
                                 constraint fk_channel_members_channel
                                     foreign key (channel_id) references channels(id) on delete cascade,
                                 constraint idx_channel_user unique (channel_id, user_id)
);

create index idx_user_id on channel_members (user_id);

create table dm_conversations (
                                  id varchar(36) primary key,
                                  user1_id varchar(36) not null,
                                  user2_id varchar(36) not null,
                                  created_at timestamp not null,
                                  constraint idx_user_pair unique (user1_id, user2_id)
);

create index idx_user1 on dm_conversations (user1_id);
create index idx_user2 on dm_conversations (user2_id);
