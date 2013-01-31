--
-- $Id: add_dept.sql,v 1.1 2003/05/23 02:18:11 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/informix/add_dept.sql,v $
-- Adding a new department to the database
--
--drop procedure add_dept;
create procedure add_dept(i_dept_name char(64)) returning 
    integer,   -- status
    integer;   -- dept_id

define esql, eisam integer;
define emesg char(80);
define v_dept_exists integer;
define v_cur_date date;
define v_dept_id integer;
define v_rows_inserted integer;

begin
    let v_dept_exists = 0;
    select 1 into v_dept_exists from department where dept_name = i_dept_name;
    if (v_dept_exists = 1) then
        raise exception -746, 0, "add_dept: Department already exists";
    end if;
    select today into v_cur_date from systables where tabid = 1;
    insert into department(
        dept_name, num_employees, crt_by_uid, mod_by_uid, crt_dttm, mod_dttm)
        values (i_dept_name, 0, 0, 0, v_cur_date, v_cur_date);
    let v_dept_id = DBINFO('sqlca.sqlerrd1');
    let v_rows_inserted = DBINFO('sqlca.sqlerrd2');
    if (v_rows_inserted != 1) then
        raise exception -746, 0, "add_dept: Insert into Department failed";
    end if;
    return 0, v_dept_id;
end
end procedure;
