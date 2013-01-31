/*==============================================================*/
/* DBMS name:      ORACLE Version 9i2                       */
/* Created on:     8/26/2004 6:32:12 PM                     */
/*==============================================================*/


create sequence S_RDM_D_SCHEMA_ID
increment by 1
start with 1
;

/*==============================================================*/
/* Table: SPSSWM_EVENTUSER                                  */
/*==============================================================*/
create table SPSSWM_EVENTUSER  (
   EVENT_USER_ID        INTEGER                        
not null,
   USER_TYPE            INTEGER,
   AUTHORIZED_USER_NAME VARCHAR2(255),
   USER_COOKIE          VARCHAR2(255),
   HOSTNAME             VARCHAR2(255),
   constraint PK_SPSSWM_EVENTUSER primary key 
(EVENT_USER_ID)
)
;

/*==============================================================*/
/* Table: SPSS_RDM_D_ENTITY                                 */
/*==============================================================*/
create table SPSS_RDM_D_ENTITY  (
   ENTY_ID              INTEGER                        not null,
   ORIGIN_DATE          DATE,
   ENTY_TYPE            CHAR(3),
   PREFIX               CHAR(3),
   FIRST_NAME           VARCHAR2(30),
   SECOND_NAME          VARCHAR2(30),
   THIRD_NAME           VARCHAR2(30),
   SUFFIX               VARCHAR2(15),
   PRIMARY_CLS          CHAR(10),
   SECONDARY_CLS        CHAR(10),
   TAX_ID               VARCHAR2(11),
   ENTY_GROUP           CHAR(35),
   REL_STATUS           VARCHAR2(15),
   PRIMARY_TEL_NBR      VARCHAR2(15),
   SCNDRY_TEL_NBR       VARCHAR2(15),
   FAX_NBR              VARCHAR2(15),
   EMAIL                VARCHAR2(80),
   LIFE_STATUS          VARCHAR2(15),
   EMP_MGT_UNIT_IND     CHAR(1),
   EN_ALL_ENTRIES_IND   CHAR(1),
   ENTY_CAT_NAME        VARCHAR2(30),
   ENTY_SUBCAT_NAME     VARCHAR2(30),
   ENTY_UNIQUE_ID       VARCHAR2(18),
   LAST_UPDATE_DT       DATE,
   CREATE_DT            DATE,
   constraint PK_SPSS_RDM_D_ENTITY primary key (ENTY_ID)
)
;

/*==============================================================*/
/* Table: SPSS_RDM_D_ENTY_LGCY_IDENT                            */
/*==============================================================*/
create table SPSS_RDM_D_ENTY_LGCY_IDENT  (
   ENTY_ID              INTEGER                        not null,
   SYS_SEQ_NBR          INTEGER                        not null,
   ENTY_LEGACY_ID       VARCHAR2(18)                   not null,
   OPER_SYS_NAME        VARCHAR2(30),
   ID_START_DATE        DATE,
   ID_END_DATE          DATE,
   constraint PK_SPSS_RDM_D_ENTY_LGCY_IDENT primary key 
(ENTY_ID, SYS_SEQ_NBR, ENTY_LEGACY_ID),
   constraint FK_SPSS_RDM_REF_25235_SPSS_RDM foreign 
key (ENTY_ID)
         references SPSS_RDM_D_ENTITY (ENTY_ID)
)
;

/*==============================================================*/
/* Table: SPSS_RDM_D_SCHEMA_VERSION                             */
/*==============================================================*/
create table SPSS_RDM_D_SCHEMA_VERSION  (
   ID                   INTEGER                        not null,
   PACKAGE              VARCHAR2(255)                  not null,
   VERSION              VARCHAR2(20)                   not null,
   OBJECT               VARCHAR2(255),
   LAST_UPDATE_DT       DATE,
   CREATE_DT            DATE,
   constraint PK_SPSS_RDM_D_SCHEMA_VERSION primary key 
(PACKAGE, VERSION)
)
;

/*==============================================================*/
/* Table: SPSSWM_EVENT                                      */
/*==============================================================*/
create table SPSSWM_EVENT  (
   EVENT_ID             INTEGER                        not null,
   EVENT_USER_ID        INTEGER,
   EVENT_CATEGORY       VARCHAR2(50),
   EVENT_NAME           VARCHAR2(50),
   RESOURCE_NAME        VARCHAR2(255),
   EVENT_TIMESTAMP      DATE,
   VISIT_ID             INTEGER,
   VISIT_START_TIMESTAMP DATE,
   constraint PK_SPSSWM_EVENT primary key (EVENT_ID),
   constraint FK_SPSSWM_E_REF_407_SPSSWM_E foreign key 
(EVENT_USER_ID)
         references SPSSWM_EVENTUSER (EVENT_USER_ID)
)
;

/*==============================================================*/
/* Table: SPSSWM_EVENTATTRIBUTEBASE                             */
/*==============================================================*/
create table SPSSWM_EVENTATTRIBUTEBASE  (
   EVENT_ATTRIBUTE_ID   INTEGER                        not null,
   EVENT_ID             INTEGER,
   ATTRIBUTE_NAME       VARCHAR2(50),
   ATTRIBUTE_VALUE      VARCHAR2(255),
   constraint PK_SPSSWM_EVENTATTRIBUTEBASE primary key 
(EVENT_ATTRIBUTE_ID),
   constraint FK_SPSSWM_E_REF_408_SPSSWM_E foreign key 
(EVENT_ID)
         references SPSSWM_EVENT (EVENT_ID)
)
;

/*==============================================================*/
/* View: SPSSWM_V_EVENTATTRIBUTE                                */
/*==============================================================*/
create or replace view SPSSWM_V_EVENTATTRIBUTE as
select 
   SPSSWM_Event.Event_ID as Event_ID,
   Event_Category,
   Event_Name,
   Resource_Name,
   Event_Timestamp,
   Visit_ID,
   Visit_Start_Timestamp,
   SPSSWM_EventUser.Event_User_ID as User_ID,
   User_Type,
   Authorized_User_Name,
   User_Cookie,
   Hostname,
   case
     when SPSSWM_EventAttributeBase.Event_Attribute_ID is 
null
   then 0
   else
     SPSSWM_EventAttributeBase.Event_Attribute_ID 
   end
   as Attribute_ID,
   Attribute_Name,
   Attribute_Value
from 
  SPSSWM_Event inner join SPSSWM_EventUser on 
(SPSSWM_Event.Event_User_ID =
                                       
SPSSWM_EventUser.Event_User_ID)
  left outer join SPSSWM_EventAttributeBase
    on (SPSSWM_Event.Event_ID = 
SPSSWM_EventAttributeBase.Event_ID)
;

/*==============================================================*/
/* View: SPSSWM_V_EVENTWITHUSER                                 */
/*==============================================================*/
create or replace view SPSSWM_V_EVENTWITHUSER as
select
SPSSWM_Event.Event_ID as Event_ID,
   Event_Category,
   Event_Name,
   Resource_Name,
   Event_Timestamp,
   Visit_ID,
   Visit_Start_Timestamp,
   SPSSWM_EventUser.Event_User_ID as User_ID,
   User_Type,
   Authorized_User_Name,
   User_Cookie,
   Hostname
from SPSSWM_Event, SPSSWM_EventUser
where SPSSWM_Event.Event_User_ID = 
SPSSWM_EventUser.Event_User_ID
;

-- Test for last line delimiter without a trailing newline
create or replace view SPSSWM_V_EVENTWITHUSER as
select SPSSWM_Event.Event_ID as 
    Event_ID,Event_Category,Event_Name,Resource_Name,Event_Timestamp,
    Visit_ID,Visit_Start_Timestamp,SPSSWM_EventUser.Event_User_ID as 
    User_ID, User_Type, Authorized_User_Name,User_Cookie,Hostname
from SPSSWM_Event, SPSSWM_EventUser
where SPSSWM_Event.Event_User_ID = SPSSWM_EventUser.Event_User_ID
;

-- Test for David's stored procedure
CREATE PROCEDURE add_dept(
    @dept_name varchar(64)
) 
RESULT( dept_id integer )   
BEGIN    
    SET TEMPORARY OPTION ON_TSQL_ERROR='Stop';

    IF EXISTS( SELECT 1
                 FROM department
                WHERE dept_name = @dept_name ) THEN
        RAISERROR 20746 'add_dept: Department already exists';
    END IF;

    INSERT INTO department( dept_name, num_employees )
    VALUES (@dept_name, 0);

    IF (SQLCODE < 0) THEN
        RAISERROR 20746 'add_dept: Insert into Department failed';
    END IF;

    SELECT @@IDENTITY;
END
;
