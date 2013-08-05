DROP TABLE IF EXISTS METRICS_USAGE_GC;

create table METRICS_USAGE_GC 
(
   USAGE_ID             VARCHAR(64)         not null,
   NAME                 VARCHAR(64)		    not null,
   GC_COUNT             BIGINT,
   GC_TIME              BIGINT,
   CREATED_FROM         VARCHAR(30)         not null,
   UPDATED_AT           DATETIME,
   UPDATED_FROM         VARCHAR(30),
   CREATED_AT           DATETIME
);

CREATE INDEX FK_METRICS_USAGE_GC ON METRICS_USAGE_GC (USAGE_ID);

alter table METRICS_USAGE_GC
   add constraint FK_GC_USAGE_ID foreign key (USAGE_ID)
      references METRICS_USAGE_RUNTIME (USAGE_ID);
