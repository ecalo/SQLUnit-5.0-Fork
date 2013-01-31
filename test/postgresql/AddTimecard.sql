-- $Id: AddTimecard.sql,v 1.1 2003/04/21 18:17:31 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/postgresql/AddTimecard.sql,v $
-- Adds a timecard record for a given employee
--
create or replace function AddTimecard(varchar, date, varchar, varchar)
returns integer as '
  declare
    i_emp_name alias for $1;
    i_log_date alias for $2;
    i_time_in alias for $3;
    i_time_out alias for $4;
    v_status integer;
    v_emp_id integer;
    v_time_in timestamp;
    v_time_out timestamp;
  begin
    v_emp_id := null;
    select emp_id into v_emp_id from employee where emp_name = i_emp_name;
    if (v_emp_id is null) then
      raise exception ''AddTimecard: Employee % does not exist'', i_emp_name;
    end if;
    if (i_time_in > i_time_out) then
      raise exception ''AddTimecard: Start time % greater than end time %'',
        i_time_in, i_time_out;
    end if;
    select 1 into v_status from timecard 
      where emp_id = v_emp_id
      and log_date = i_log_date;
    if FOUND then
      raise exception ''AddTimecard: Employee % timecard for % already exists'',
        i_emp_name, i_log_date;
    end if;
    v_time_in := CAST(i_log_date||'' ''||i_time_in as timestamp);
    if (i_time_out is null) then
      v_time_out := v_time_in;
    else
      v_time_out := CAST(i_log_date||'' ''||i_time_out as timestamp);
    end if;
    insert into timecard (emp_id, log_date, time_in, time_out) values (
      v_emp_id, i_log_date, v_time_in, v_time_out);
    get diagnostics v_status := ROW_COUNT;
    if (v_status != 1) then
      raise exception ''AddTimecard: Could not insert timecard record'';
    end if;
    return v_emp_id;
  end;
' language 'plpgsql';
