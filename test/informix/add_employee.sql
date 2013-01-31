--
-- $Id: add_employee.sql,v 1.1 2003/05/23 02:18:11 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/informix/add_employee.sql,v $
-- Adding a new employee to the database
--
--drop procedure add_employee;
create procedure add_employee(
    i_dept_name char(64), 
    i_emp_name char(64),
    i_emp_dob date,
    i_emp_ssn char(9),
    i_emp_payrate money(9,2))
    returning
    integer,        -- status
    integer;        -- emp_id

define esql, eisam integer;
define emesg char(80);
define v_dept_id integer;
define v_emp_id integer;
define v_num_inserted integer;

begin
    let v_dept_id = null;
    select dept_id into v_dept_id from department where dept_name = i_dept_name;
    if (v_dept_id is null) then
        raise exception -746, 0, "add_employee: Non-existent department";
    end if;
    let v_emp_id = null;
    select emp_id into v_emp_id from employee where emp_name = i_emp_name;
    if (v_emp_id is not null) then
        raise exception -746, 0, "add_employee: Employee already exists";
    end if;
    insert into employee(
        dept_id, emp_name, emp_dob, emp_ssn, rate_per_hour)
        values (v_dept_id, i_emp_name, i_emp_dob, i_emp_ssn, i_emp_payrate);
    let v_emp_id = DBINFO('sqlca.sqlerrd1');
    let v_num_inserted = DBINFO('sqlca.sqlerrd2');
    if (v_num_inserted != 1) then
        raise exception -746, 0, "add_employee: Error inserting employee";
    end if
    return 0, v_emp_id;
end
end procedure;
