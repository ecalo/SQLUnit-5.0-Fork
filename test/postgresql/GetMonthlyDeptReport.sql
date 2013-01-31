-- $Id: GetMonthlyDeptReport.sql,v 1.2 2003/06/09 19:50:37 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/postgresql/GetMonthlyDeptReport.sql,v $
-- Prints a monthly report for the department
--
drop type deptrep_rec cascade;
create type deptrep_rec as (
  dept_name varchar, emp_name varchar, hours_worked integer, 
  pay_rate decimal, total_pay decimal);
create or replace function GetMonthlyDeptReport(varchar, date, date)
returns setof deptrep_rec as '
  declare
    i_dept_name alias for $1;
    i_start_dt alias for $2;
    i_stop_dt alias for $3;
    v_dept_id integer;
    v_emp_id integer;
    v_emp_name varchar;
    v_days integer;
    v_hrs integer;
    v_mins integer;
    v_report_rec deptrep_rec%rowtype;
    v_emp_rec employee%rowtype;
    v_hours_worked interval;
  begin
    v_dept_id := null;
    select dept_id into v_dept_id from department where dept_name = i_dept_name;
    if (v_dept_id is null) then
      raise exception ''GetMonthlyDeptReport: Invalid department %'', 
        i_dept_name;
    end if;
    for v_emp_rec in select * from employee where dept_id = v_dept_id 
        order by emp_name loop
      v_report_rec.dept_name := i_dept_name;
      v_report_rec.emp_name := v_emp_rec.emp_name;
      v_report_rec.pay_rate := v_emp_rec.rate_per_hour;
      v_emp_id := v_emp_rec.emp_id;
      select sum(age(cast(''01-01-01 '' || time_out as timestamp), 
        cast(''01-01-01 '' || time_in as timestamp))) 
        into v_hours_worked
        from timecard
        where emp_id = v_emp_rec.emp_id
        and log_date >= i_start_dt
        and log_date <= i_stop_dt;
      if (v_hours_worked is not null) then
        v_days := date_part(''day'', v_hours_worked);
        v_hrs := date_part(''hour'', v_hours_worked);
        v_mins := date_part(''minute'', v_hours_worked);
        v_report_rec.hours_worked := (v_days * 12) + v_hrs + (v_mins / 60);
        v_report_rec.total_pay := 
          v_report_rec.pay_rate * v_report_rec.hours_worked;
      else
        v_report_rec.hours_worked := 0;
        v_report_rec.total_pay := 0.00;
      end if;
      return next v_report_rec;
    end loop;
    return;
  end;
' language 'plpgsql';
