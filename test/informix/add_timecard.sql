--
-- $Id: add_timecard.sql,v 1.1 2003/05/23 02:18:11 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/informix/add_timecard.sql,v $
-- Adds a timecard record for given employee
--
--drop procedure add_timecard;
create procedure add_timecard(
    i_emp_name char(64),
    i_log_date date,
    i_num_hours integer)
    returning
    integer,            -- status
    integer;            -- timecard id

define esql, eisam integer;
define emesg char(80);
define v_timecard_exists integer;
define v_emp_id integer;
define v_timecard_id integer;
define v_num_inserted integer;

begin
    if (i_num_hours > 24) then
        raise exception -746, 0, "add_timecard: Too many hours, slow down";
    end if;
    if (i_num_hours < 0) then
        raise exception -746, 0, "add_timecard: Too few hours, speed up";
    end if;
    let v_emp_id = null;
    select emp_id into v_emp_id from employee where emp_name = i_emp_name;
    if (v_emp_id is null) then
        raise exception -746, 0, "add_timecard: Nonexistent employee";
    end if;
    let v_timecard_exists = 0;
    select 1 into v_timecard_exists from timecard
        where emp_id = v_emp_id
        and log_date = i_log_date;
    if (v_timecard_exists = 1) then
        raise exception -746, 0, "add_timecard: Timecard already exists";
    end if;
    insert into timecard(emp_id, log_date, num_hours) values (
        v_emp_id, i_log_date, i_num_hours);
    let v_timecard_id = DBINFO('sqlca.sqlerrd1');
    let v_num_inserted = DBINFO('sqlca.sqlerrd2');
    if (v_num_inserted != 1) then
        raise exception -746, 0, "add_timecard: Error inserting timecard";
    end if;
    return 0, v_timecard_id;
end
end procedure;
