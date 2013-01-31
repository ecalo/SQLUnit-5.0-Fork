--
-- $Id: schema.sql,v 1.3 2004/02/05 17:45:55 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/postgresql/schema.sql,v $
-- Schema for a simple HR database
--
drop table department cascade;
drop table employee cascade;
drop table timecard cascade;
drop sequence department_dept_id_seq;
drop sequence employee_emp_id_seq;

create sequence department_dept_id_seq;
create table department (
  dept_id integer default nextval('department_dept_id_seq') not null,
  dept_name varchar(64),
  num_employees integer,
  crt_by_uid integer not null,
  mod_by_uid integer not null,
  crt_dttm timestamp not null,
  mod_dttm timestamp not null
);
create unique index ux_department on department(dept_id);

create sequence employee_emp_id_seq;
create table employee (
  dept_id integer,
  emp_id integer default nextval('employee_emp_id_seq') not null,
  emp_name varchar(64),
  emp_dob date,
  emp_ssn varchar(9),
  rate_per_hour decimal(6,2),
  location point default '(0,0)' not null
);
create unique index ux_employee on employee(emp_id);
create unique index ux2_employee on employee(emp_name);
create index ax1_employee on employee(emp_id, dept_id);

create table timecard (
  emp_id integer not null,
  log_date date,
  time_in time not null,
  time_out time
);
create unique index ux_timecard on timecard(emp_id, log_date);

-- for foreach testing
create table foreachtest (
  id integer not null,
  name varchar(256)
);

