-- $Id: GetAllEmpForDept.sql,v 1.2 2003/05/02 19:38:58 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/postgresql/GetAllEmpForDept.sql,v $
-- Returns a list of all employees for a given department
--
create or replace function GetAllEmpForDept(varchar) returns setof employee as '
  declare
    i_dept_name alias for $1;
    v_dept_id integer;
    v_rec employee%rowtype;
  begin
    v_dept_id := null;
    select dept_id into v_dept_id from department where dept_name = i_dept_name;
    if (v_dept_id is null) then
      raise exception ''GetAllEmpForDept: department % not found'', i_dept_name;
    end if;
    for v_rec in select * from employee where dept_id = v_dept_id
        order by emp_id loop
      return next v_rec;
    end loop;
    return;
  end;
' language 'plpgsql';
