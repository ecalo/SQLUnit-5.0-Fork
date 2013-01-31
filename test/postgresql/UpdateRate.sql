-- $Id: UpdateRate.sql,v 1.1 2003/04/21 18:17:31 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/postgresql/UpdateRate.sql,v $
-- Updates the rate for an employee
--
create or replace function UpdateRate(varchar,decimal) returns decimal as '
  declare
    i_emp_name alias for $1;
    i_emp_rate alias for $2;
    v_status integer;
    v_old_rate decimal;
  begin
    v_old_rate = null;
    select rate_per_hour into v_old_rate from employee 
      where emp_name = i_emp_name;
    if (v_old_rate is null) then
      raise exception ''UpdateRate: Employee % does not exist'', i_emp_name;
    end if;
    update employee
      set rate_per_hour = i_emp_rate
      where emp_name = i_emp_name;
    get diagnostics v_status = ROW_COUNT;
    if (v_status != 1) then
      raise exception ''UpdateRate: Could not update rate for % to %'',
        i_emp_name, i_emp_rate;
    end if;
    return v_old_rate;
  end;
' language 'plpgsql';
