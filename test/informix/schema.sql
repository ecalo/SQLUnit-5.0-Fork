-- 
-- $Id: schema.sql,v 1.2 2003/05/24 18:43:17 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/informix/schema.sql,v $
--
--drop table department;
--drop table employee;
--drop table timecard;

create table department (
  dept_id serial not null,
  dept_name char(64),
  num_employees integer,
  crt_by_uid integer not null,
  mod_by_uid integer not null,
  crt_dttm date not null,
  mod_dttm date not null
);
create unique index ux_department on department(dept_id);

create table employee (
  dept_id integer,
  emp_id serial not null,
  emp_name char(64),
  emp_dob date,
  emp_ssn char(9),
  rate_per_hour decimal(6,2)
);
create unique index ux_employee on employee(emp_id);
create unique index ux1_employee on employee(emp_id, dept_id);
create index ax1_employee on employee(emp_name);

create table timecard (
  timecard_id serial not null,
  emp_id integer not null,
  log_date date,
  num_hours integer default 0 not null
);
create unique index ux_timecard on timecard(emp_id, log_date);
