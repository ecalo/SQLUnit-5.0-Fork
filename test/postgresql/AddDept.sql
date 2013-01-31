-- $Id: AddDept.sql,v 1.1 2003/04/21 18:17:31 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/postgresql/AddDept.sql,v $
-- Adding a new department to the database
--
create or replace function AddDept(varchar) returns integer as '
  declare
    i_dept_name alias for $1;
    v_admin_uid integer;
    v_status integer;
    v_new_dept_id integer;
  begin
    v_admin_uid := null;
    select 1 into v_status from department where dept_name = i_dept_name;
    if FOUND then
      raise exception ''AddDept: Department % already exists'', i_dept_name;
    end if;
    select emp_id into v_admin_uid from employee where emp_name=''Admin'';
    if (v_admin_uid is null) then
      insert into employee 
        (dept_id, emp_name, emp_dob, emp_ssn, rate_per_hour)
        values (-1, ''Admin'', ''1900-01-01'', ''000000000'', 0.00);
      get diagnostics v_status := ROW_COUNT;
      if (v_status != 1) then
        raise exception 
          ''AddDept: Admin account not available and could not be created'';
      end if;
      v_admin_uid := -1;
    end if;
    insert into department 
      (dept_name, num_employees, crt_by_uid, mod_by_uid, crt_dttm, mod_dttm)
      values(i_dept_name, 0, v_admin_uid, v_admin_uid, NOW(), NOW());
    get diagnostics v_status := ROW_COUNT;
    if (v_status != 1) then
      raise exception ''AddDept: Could not insert new dept record'';
    end if;
    v_new_dept_id := currval(''department_dept_id_seq'');
    return v_new_dept_id;
  end;
' language 'plpgsql';
