DROP TABLE IF EXISTS METRICS_USAGE_HEAP;

create table METRICS_USAGE_HEAP 
(
   USAGE_ID             	VARCHAR(64)         not null,
   NAME                 	VARCHAR(64)    		not null,
   MEM_MAX              	BIGINT,
   MEM_USED             	BIGINT,
   CREATED_FROM           	VARCHAR(30)         not null,
   UPDATED_AT           	DATETIME,
   UPDATED_FROM           	VARCHAR(30),
   CREATED_AT           	DATETIME
);


CREATE INDEX FK_METRICS_USAGE_HEAP ON METRICS_USAGE_HEAP (USAGE_ID);


alter table METRICS_USAGE_HEAP
   add constraint FK_HEAP_USAGE_ID foreign key (USAGE_ID)
      references METRICS_USAGE_RUNTIME (USAGE_ID);



