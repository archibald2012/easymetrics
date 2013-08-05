DROP TABLE IF EXISTS METRICS_USAGE_RUNTIME;

create table METRICS_USAGE_RUNTIME 
(
   USAGE_ID             VARCHAR(64)         not null,
   RECORD_ID            VARCHAR(64)         not null,
   CHECK_TIME           DATETIME 			not null,
   CPU_COUNT            BIGINT              not null,
   THREAD_COUNT         BIGINT              not null,
   UP_TIME              BIGINT              not null,
   CPU_TIME             BIGINT              not null,
   USER_TIME            BIGINT              not null,
   HEAP_MAX             BIGINT              not null,
   HEAP_USED            BIGINT              not null,
   NON_HEAP_MAX         BIGINT              not null,
   NON_HEAP_USED        BIGINT              not null,
   CREATED_FROM         VARCHAR(30)         not null,
   UPDATED_AT           DATETIME,
   UPDATED_FROM         VARCHAR(30),
   CREATED_AT           DATETIME,
   PRIMARY KEY (USAGE_ID)
);


CREATE INDEX FK_METRICS_USAGE_RUNTIME ON METRICS_USAGE_RUNTIME (RECORD_ID);

alter table METRICS_USAGE_RUNTIME
   add constraint FK_METRICS_USAGE_RUNTIME foreign key (RECORD_ID)
      references METRICS_RECORD (RECORD_ID);



