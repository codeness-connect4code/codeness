-- 테이블 생성

CREATE TABLE IF NOT EXISTS batch_job_execution_seq
(
    ID         bigint not null,
    UNIQUE_KEY char   not null,
    constraint UNIQUE_KEY_UN
        unique (UNIQUE_KEY)
);

CREATE TABLE IF NOT EXISTS batch_job_instance
(
    JOB_INSTANCE_ID bigint       not null
        primary key,
    VERSION         bigint       null,
    JOB_NAME        varchar(100) not null,
    JOB_KEY         varchar(32)  not null,
    constraint JOB_INST_UN
        unique (JOB_NAME, JOB_KEY)
);

CREATE TABLE IF NOT EXISTS batch_job_execution
(
    JOB_EXECUTION_ID bigint        not null
        primary key,
    VERSION          bigint        null,
    JOB_INSTANCE_ID  bigint        not null,
    CREATE_TIME      datetime(6)   not null,
    START_TIME       datetime(6)   null,
    END_TIME         datetime(6)   null,
    STATUS           varchar(10)   null,
    EXIT_CODE        varchar(2500) null,
    EXIT_MESSAGE     varchar(2500) null,
    LAST_UPDATED     datetime(6)   null,
    constraint JOB_INST_EXEC_FK
        foreign key (JOB_INSTANCE_ID) references batch_job_instance (JOB_INSTANCE_ID)
);

CREATE TABLE IF NOT EXISTS batch_job_execution_context
(
    JOB_EXECUTION_ID   bigint        not null
        primary key,
    SHORT_CONTEXT      varchar(2500) not null,
    SERIALIZED_CONTEXT text          null,
    constraint JOB_EXEC_CTX_FK
        foreign key (JOB_EXECUTION_ID) references batch_job_execution (JOB_EXECUTION_ID)
);

CREATE TABLE IF NOT EXISTS batch_job_execution_params
(
    JOB_EXECUTION_ID bigint        not null,
    PARAMETER_NAME   varchar(100)  not null,
    PARAMETER_TYPE   varchar(100)  not null,
    PARAMETER_VALUE  varchar(2500) null,
    IDENTIFYING      char          not null,
    constraint JOB_EXEC_PARAMS_FK
        foreign key (JOB_EXECUTION_ID) references batch_job_execution (JOB_EXECUTION_ID)
);

CREATE TABLE IF NOT EXISTS batch_job_seq
(
    ID         bigint not null,
    UNIQUE_KEY char   not null,
    constraint UNIQUE_KEY_UN
        unique (UNIQUE_KEY)
);

CREATE TABLE IF NOT EXISTS batch_step_execution
(
    STEP_EXECUTION_ID  bigint        not null
        primary key,
    VERSION            bigint        not null,
    STEP_NAME          varchar(100)  not null,
    JOB_EXECUTION_ID   bigint        not null,
    CREATE_TIME        datetime(6)   not null,
    START_TIME         datetime(6)   null,
    END_TIME           datetime(6)   null,
    STATUS             varchar(10)   null,
    COMMIT_COUNT       bigint        null,
    READ_COUNT         bigint        null,
    FILTER_COUNT       bigint        null,
    WRITE_COUNT        bigint        null,
    READ_SKIP_COUNT    bigint        null,
    WRITE_SKIP_COUNT   bigint        null,
    PROCESS_SKIP_COUNT bigint        null,
    ROLLBACK_COUNT     bigint        null,
    EXIT_CODE          varchar(2500) null,
    EXIT_MESSAGE       varchar(2500) null,
    LAST_UPDATED       datetime(6)   null,
    constraint JOB_EXEC_STEP_FK
        foreign key (JOB_EXECUTION_ID) references batch_job_execution (JOB_EXECUTION_ID)
);

CREATE TABLE IF NOT EXISTS batch_step_execution_context
(
    STEP_EXECUTION_ID  bigint        not null
        primary key,
    SHORT_CONTEXT      varchar(2500) not null,
    SERIALIZED_CONTEXT text          null,
    constraint STEP_EXEC_CTX_FK
        foreign key (STEP_EXECUTION_ID) references batch_step_execution (STEP_EXECUTION_ID)
);

CREATE TABLE IF NOT EXISTS batch_step_execution_seq
(
    ID         bigint not null,
    UNIQUE_KEY char   not null,
    constraint UNIQUE_KEY_UN
        unique (UNIQUE_KEY)
);

CREATE TABLE IF NOT EXISTS user
(
    career        int                                                                                                not null,
    mbti          varchar(5)                                                                                         null,
    created_at    datetime(6)                                                                                        null,
    id            bigint auto_increment
        primary key,
    modified_at   datetime(6)                                                                                        null,
    account       varchar(30)                                                                                        null,
    bank_name     varchar(30)                                                                                        null,
    email         varchar(30)                                                                                        not null,
    name          varchar(30)                                                                                        not null,
    phone_number  varchar(30)                                                                                        null,
    region        varchar(30)                                                                                        null,
    user_nickname varchar(30)                                                                                        not null,
    site_link     varchar(100)                                                                                       null,
    google_token  varchar(255)                                                                                       null,
    password      varchar(255)                                                                                       null,
    field         enum ('AI', 'BACKEND', 'EMBEDDED_SYSTEMS', 'FRONTEND', 'GAME', 'NETWORK_SECURITY', 'SERVER_INFRA') null,
    provider      enum ('GOOGLE', 'LOCAL')                                                                           not null,
    role          enum ('ADMIN', 'MENTEE', 'MENTOR')                                                                 not null,
    user_status   enum ('ACTIVE', 'LEAVE')                                                                           not null,
    constraint UKob8kqyqqgmefl0aco34akdtpe
        unique (email)
);

CREATE TABLE IF NOT EXISTS chat_room_history
(
    created_at   datetime(6)  null,
    expire_at    datetime(6)  not null,
    id           bigint auto_increment
        primary key,
    user_id      bigint       not null,
    chat_room_id varchar(100) not null,
    constraint FK6c5h1f73sp53mvt49er6radi9
        foreign key (user_id) references user (id)
);

CREATE TABLE IF NOT EXISTS file
(
    created_at    datetime(6)                       null,
    file_size     bigint                            not null,
    id            bigint auto_increment
        primary key,
    modified_at   datetime(6)                       null,
    user_id       bigint                            null,
    file_key      varchar(255)                      not null,
    file_name     varchar(255)                      not null,
    file_path     varchar(255)                      not null,
    file_type     varchar(255)                      not null,
    file_category enum ('EMPLOYEE_CARD', 'PROFILE') not null,
    constraint FKinph5hu8ryc97hbs75ym9sm7t
        foreign key (user_id) references user (id)
);

CREATE TABLE IF NOT EXISTS mentor_request
(
    career        int                                                                                                not null,
    created_at    datetime(6)                                                                                        null,
    id            bigint auto_increment
        primary key,
    user_id       bigint                                                                                             not null,
    company       varchar(30)                                                                                        not null,
    company_email varchar(30)                                                                                        not null,
    phone_number  varchar(30)                                                                                        not null,
    position      varchar(30)                                                                                        not null,
    field         enum ('AI', 'BACKEND', 'EMBEDDED_SYSTEMS', 'FRONTEND', 'GAME', 'NETWORK_SECURITY', 'SERVER_INFRA') null,
    is_accepted   enum ('ACCEPTED', 'REJECTED', 'WAITING')                                                           not null,
    constraint FKp8qax98j706u1ip4j4t4aqtru
        foreign key (user_id) references user (id)
);

CREATE TABLE IF NOT EXISTS mentoring_post
(
    career                int                                                                                                not null,
    end_date              date                                                                                               not null,
    end_time              time(6)                                                                                            not null,
    price                 decimal(38, 2)                                                                                     not null,
    start_date            date                                                                                               not null,
    start_time            time(6)                                                                                            not null,
    created_at            datetime(6)                                                                                        null,
    id                    bigint auto_increment
        primary key,
    mentor_id             bigint                                                                                             null,
    company               varchar(30)                                                                                        not null,
    region                varchar(30)                                                                                        not null,
    title                 varchar(30)                                                                                        not null,
    description           varchar(300)                                                                                       not null,
    field                 enum ('AI', 'BACKEND', 'EMBEDDED_SYSTEMS', 'FRONTEND', 'GAME', 'NETWORK_SECURITY', 'SERVER_INFRA') not null,
    mentoring_post_status enum ('DELETED', 'DISPLAYED')                                                                      not null,
    constraint FK79n7mdoed7h4gff33ugfntg5
        foreign key (mentor_id) references user (id)
);

create index idx_mentoring_post_field_created
    on mentoring_post (field, created_at);

CREATE TABLE IF NOT EXISTS mentoring_schedule
(
    mentoring_date            date                                     not null,
    mentoring_time            time(6)                                  not null,
    created_at                datetime(6)                              null,
    id                        bigint auto_increment
        primary key,
    mentoring_post_id         bigint                                   null,
    booked_status             enum ('BOOKED', 'EMPTY', 'IN_PROGRESS')  not null,
    mentoring_schedule_status enum ('DELETED', 'DISPLAYED', 'EXPIRED') not null,
    constraint FK4k42qd2s4s9qqgripjrk0oxmy
        foreign key (mentoring_post_id) references mentoring_post (id)
);

CREATE TABLE IF NOT EXISTS payment
(
    payment_cost          decimal(38, 2) not null,
    canceled_at           datetime(6)    null,
    created_at            datetime(6)    null,
    id                    bigint auto_increment
        primary key,
    mentee_id             bigint         null,
    mentoring_schedule_id bigint         null,
    imp_uid               varchar(255)   null,
    payment_card          varchar(255)   not null,
    pg_tid                varchar(255)   null,
    constraint FK2t0dlomne8re3lwktri28qd9a
        foreign key (mentoring_schedule_id) references mentoring_schedule (id),
    constraint FKrngcqw4e5w7yu71xv9ahkxkt2
        foreign key (mentee_id) references user (id)
);

CREATE TABLE IF NOT EXISTS payment_history
(
    payment_cost   decimal(38, 2)               not null,
    canceled_at    datetime(6)                  null,
    created_at     datetime(6)                  null,
    id             bigint auto_increment
        primary key,
    mentor_id      bigint                       null,
    payment_id     bigint                       null,
    payment_card   varchar(255)                 not null,
    pg_tid         varchar(255)                 not null,
    payment_status enum ('CANCEL', 'COMPLETE')  not null,
    review_status  enum ('COMPLETE', 'NOT_YET') not null,
    constraint UK2q479f1b6k3gbwl1oaoakhi53
        unique (payment_id),
    constraint FKrgfm32kn93gp9q6e5jcoaf5f7
        foreign key (payment_id) references payment (id),
    constraint FKsne1ur1r6g5winco48geagg2c
        foreign key (mentor_id) references user (id)
);

CREATE TABLE IF NOT EXISTS post
(
    created_at  datetime(6)                         null,
    id          bigint auto_increment
        primary key,
    modified_at datetime(6)                         null,
    user_id     bigint                              null,
    view        bigint                              not null,
    title       varchar(50)                         not null,
    content     varchar(255)                        not null,
    writer      varchar(255)                        not null,
    post_status enum ('DELETED', 'DISPLAYED')       not null,
    post_type   enum ('FREE', 'NOTICE', 'QUESTION') not null,
    constraint FK72mt33dhhs48hf9gcqrq4fxte
        foreign key (user_id) references user (id)
);

CREATE TABLE IF NOT EXISTS comment
(
    created_at         datetime(6)                   null,
    id                 bigint auto_increment
        primary key,
    modified_at        datetime(6)                   null,
    post_id            bigint                        null,
    user_id            bigint                        null,
    content            varchar(100)                  not null,
    writer_profile_url varchar(255)                  null,
    comment_status     enum ('DELETED', 'DISPLAYED') not null,
    constraint FK8kcum44fvpupyw6f5baccx25c
        foreign key (user_id) references user (id),
    constraint FKs1slvnkuemjsq2kj4h3vhx7i1
        foreign key (post_id) references post (id)
);

CREATE TABLE IF NOT EXISTS review
(
    is_deleted         bit          not null,
    star_rating        int          not null,
    created_at         datetime(6)  null,
    id                 bigint auto_increment
        primary key,
    mentoring_post_id  bigint       not null,
    modified_at        datetime(6)  null,
    payment_history_id bigint       not null,
    user_id            bigint       not null,
    review_content     varchar(300) not null,
    constraint FK84bxf1h3j8i4uyugd0lylf56g
        foreign key (payment_history_id) references payment_history (id),
    constraint FKeojjemrwfsg6wiscy8t8ybir5
        foreign key (mentoring_post_id) references mentoring_post (id),
    constraint FKiyf57dy48lyiftdrf7y87rnxi
        foreign key (user_id) references user (id)
);

CREATE TABLE IF NOT EXISTS settlement
(
    created_at            datetime(6)                                    null,
    id                    bigint auto_increment
        primary key,
    mentor_id             bigint                                         null,
    payment_history_id    bigint                                         null,
    settlement_request_at datetime(6)                                    null,
    account               varchar(255)                                   null,
    bank_name             varchar(255)                                   null,
    settlement_status     enum ('COMPLETE', 'PROCESSING', 'UNPROCESSED') not null,
    constraint UKqbu0cbfg3olo31bs9xuqc7hv
        unique (payment_history_id),
    constraint FK11pucasb1xk2b2jx2uwxt6rf0
        foreign key (mentor_id) references user (id),
    constraint FK3dsrovtymclmf3sgj7m4abudb
        foreign key (payment_history_id) references payment_history (id)
);

