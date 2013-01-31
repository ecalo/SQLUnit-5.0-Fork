-- $Id: GetEmployeeByName.sql,v 1.3 2003/06/09 19:50:37 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/postgresql/GetEmployeeByName.sql,v $
-- Returns an employee record given the employee's name
--
drop type emp_rec cascade;
create type emp_rec as (
  emp_id integer, emp_name varchar, dept_name varchar, 
  emp_dob date, emp_ssn varchar, rate_per_hour decimal);
create or replace function GetEmployeeByName(varchar) 
returns setof emp_rec as '
  declare
    i_emp_name alias for $1;
    v_rec emp_rec%rowtype;
  begin
    for v_rec in select
      employee.emp_id,
      employee.emp_name,
      department.dept_name,
      employee.emp_dob,
      employee.emp_ssn,
      employee.rate_per_hour,
      employee.location
    from employee, department
    where department.dept_id = employee.dept_id
      and employee.emp_name = i_emp_name loop
      return next v_rec;
    end loop;
    return;
  end;
' language 'plpgsql';
