-- 
-- $Id: get_emp_in_dept.sql,v 1.1 2003/05/23 02:18:11 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/informix/get_emp_in_dept.sql,v $
-- Returns all employees for a given department
--
--drop procedure get_emp_in_dept;
create procedure get_emp_in_dept(i_dept_name char(64)) 
    returning
    integer,     -- dept_id
    integer,     -- emp_id
    char(64),    -- emp_name
    date,        -- emp_dob
    char(9),     -- emp_ssn
    money(6,2); -- rate_per_hour

define esql, eisam integer;
define emesg char(80);
define v_dept_id integer;
define v_emp_id integer;
define v_emp_name char(64);
define v_emp_dob date;
define v_emp_ssn char(9);
define v_rate_per_hour money(6,2);

begin
    let v_dept_id = null;
    select dept_id into v_dept_id from department where dept_name = i_dept_name;
    if (v_dept_id is null) then
        raise exception -746, 0, "get_emp_in_dept: Nonexistent Department";
    end if;
    foreach 
        select emp_id, emp_name, emp_dob, emp_ssn, rate_per_hour
        into v_emp_id, v_emp_name, v_emp_dob, v_emp_ssn, v_rate_per_hour
        from employee
        where dept_id = v_dept_id
        order by emp_id

        return v_dept_id, v_emp_id, v_emp_name, v_emp_dob, v_emp_ssn, 
            v_rate_per_hour with resume;

    end foreach;
end 
end procedure;
