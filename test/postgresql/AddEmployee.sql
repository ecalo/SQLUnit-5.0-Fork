-- $Id: AddEmployee.sql,v 1.2 2003/06/09 19:50:36 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/postgresql/AddEmployee.sql,v $
-- Creates an employee record in the database
--
create or replace function AddEmployee(varchar, varchar, date, varchar,
    decimal, float, float) returns integer as '
  declare
    i_dept_name alias for $1;
    i_emp_name alias for $2;
    i_emp_dob alias for $3;
    i_emp_ssn alias for $4;
    i_emp_payrate alias for $5;
    i_lat alias for $6;
    i_long alias for $7;
    v_dept_id integer;
    v_status integer;
    v_new_emp_id integer;
  begin
    v_dept_id := null;
    select dept_id into v_dept_id from department where dept_name = i_dept_name;
    if (v_dept_id is null) then
      raise exception ''AddEmployee: Invalid department name'';
    end if;
    insert into employee (dept_id, emp_name, emp_dob, emp_ssn, rate_per_hour,
      location) values (v_dept_id, i_emp_name, i_emp_dob, i_emp_ssn, 
      i_emp_payrate, point(i_lat, i_long));
    get diagnostics v_status := ROW_COUNT;
    if (v_status != 1) then
      raise exception ''AddEmployee: Could not insert new employee record'';
    end if;
    v_new_emp_id := currval(''employee_emp_id_seq'');
    return v_new_emp_id;
  end;
' language 'plpgsql';
