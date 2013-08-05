DROP TABLE IF EXISTS METRICS_USAGE_THREAD;

create table METRICS_USAGE_THREAD 
(
   USAGE_ID             VARCHAR(64)         not null,
   NAME                 VARCHAR(64)    		not null,
   STATE                VARCHAR(64),
   CPU_TIME             BIGINT,
   USER_TIME            BIGINT,
   CREATED_FROM         VARCHAR(30)         not null,
   UPDATED_AT           DATETIME,
   UPDATED_FROM         VARCHAR(30),
   CREATED_AT           DATETIME
);


CREATE INDEX FK_METRICS_USAGE_THREAD ON METRICS_USAGE_THREAD (USAGE_ID);


alter table METRICS_USAGE_THREAD
   add constraint FK_THREAD_USAGE_ID foreign key (USAGE_ID)
      references METRICS_USAGE_RUNTIME (USAGE_ID);
