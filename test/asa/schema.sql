-- $Id: schema.sql,v 1.1 2004/07/09 03:12:44 dfishburn Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/asa/schema.sql,v $
-- $Revision: 1.1 $
-- Tables used by test suite

IF EXISTS( SELECT 1
             FROM sys.systable st KEY JOIN sys.sysuserperm sup
            WHERE st.table_name = 'department'
              AND sup.user_name = user_name()                 ) THEN
    DROP TABLE department;
END IF
;

IF EXISTS( SELECT 1
             FROM sys.systable st KEY JOIN sys.sysuserperm sup
            WHERE st.table_name = 'employee'
              AND sup.user_name = user_name()                 ) THEN
    DROP TABLE employee;
END IF
;

IF EXISTS( SELECT 1
             FROM sys.systable st KEY JOIN sys.sysuserperm sup
            WHERE st.table_name = 'timecard'
              AND sup.user_name = user_name()                 ) THEN
    DROP TABLE timecard;
END IF
;

CREATE TABLE department (
  dept_id       integer      NOT NULL DEFAULT AUTOINCREMENT,
  dept_name     varchar(64)  NULL,
  num_employees integer      NULL,
  crt_by_uid    varchar(128) NOT NULL DEFAULT CURRENT USER,
  mod_by_uid    varchar(128) NOT NULL DEFAULT LAST USER,
  crt_dttm      datetime     NOT NULL DEFAULT CURRENT timestamp,
  mod_dttm      datetime     NOT NULL DEFAULT timestamp,
  PRIMARY KEY( dept_id )
)
;

CREATE TABLE employee (
  emp_id        integer      NOT NULL DEFAULT AUTOINCREMENT,
  dept_id       integer      NULL,
  emp_name      varchar(64)  NULL,
  emp_dob       date         NULL,
  emp_ssn       varchar(9)   NULL,
  rate_per_hour decimal(6,2) NULL,
  crt_dttm      datetime     NOT NULL DEFAULT CURRENT timestamp,
  mod_dttm      datetime     NOT NULL DEFAULT timestamp,
  PRIMARY KEY( emp_id )
)
;
CREATE INDEX ax1_employee ON employee(emp_name)
;

CREATE TABLE timecard (
  timecard_id integer  NOT NULL DEFAULT AUTOINCREMENT,
  emp_id      integer  NOT NULL,
  log_date    date     NOT NULL,
  num_hours   integer  NOT NULL DEFAULT 0,
  crt_dttm    datetime NOT NULL DEFAULT CURRENT timestamp,
  mod_dttm    datetime NOT NULL DEFAULT timestamp,
  PRIMARY KEY( timecard_id )
)
;
CREATE UNIQUE INDEX ux_timecard ON timecard(emp_id, log_date)
;

