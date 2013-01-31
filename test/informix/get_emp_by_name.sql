-- 
-- $Id: get_emp_by_name.sql,v 1.1 2003/05/23 02:18:11 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/informix/get_emp_by_name.sql,v $
-- Returns the employee record for the named employee
--
--drop procedure get_emp_by_name;
create procedure get_emp_by_name(i_emp_name char(64)) returning
    integer,      -- status
    integer,      -- emp_id
    char(64),     -- emp_name
    char(64),     -- dept_name
    date,         -- emp_dob
    char(9),      -- emp_ssn
    decimal(9,2); -- rate_per_hour

define esql, eisam integer;
define emesg char(80);
define v_emp_name char(64);
define v_emp_id integer;
define v_dept_name char(64);
define v_emp_dob date;
define v_emp_ssn char(9);
define v_rate_per_hour decimal(9,2);

begin
    let v_emp_name = null;
    select e.emp_id, e.emp_name, d.dept_name, e.emp_dob, e.emp_ssn, 
            e.rate_per_hour
        into v_emp_id, v_emp_name, v_dept_name, v_emp_dob, v_emp_ssn,
            v_rate_per_hour
        from department d, employee e
        where e.dept_id = d.dept_id
        and e.emp_name = i_emp_name;
    if (v_emp_name is null) then
        raise exception -746, 0, "get_emp_by_name: Nonexistent employee";
    end if;
    return 0, v_emp_id, v_emp_name, v_dept_name, v_emp_dob, v_emp_ssn, 
        v_rate_per_hour;
end
end procedure;
