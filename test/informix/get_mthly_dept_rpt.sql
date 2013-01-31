-- 
-- $Id: get_mthly_dept_rpt.sql,v 1.1 2003/05/23 02:18:11 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/informix/get_mthly_dept_rpt.sql,v $
-- Prints a monthly report for a given department
--
--drop procedure get_mthly_dept_rpt;
create procedure get_mthly_dept_rpt(
    i_dept_name char(64), i_date_from date, i_date_to date) returning
    char(64),          -- dept_name
    date,              -- start date
    date,              -- stop date
    integer,           -- emp_id
    char(64),          -- emp_name
    integer,           -- total_hours_worked
    decimal(9,2),      -- rate_per_hour
    decimal(11,2);     -- total_amt_paid

define esql, eisam integer;
define emesg char(80);
define v_dept_id integer;
define v_emp_id integer;
define v_emp_name char(64);
define v_rate_per_hour decimal(9,2);
define v_hrs_worked integer;
define v_tot_hrs integer;
define v_tot_amt decimal(11,2);

begin
    let v_dept_id = null;
    select dept_id into v_dept_id from department where dept_name = i_dept_name;
    if (v_dept_id is null) then
        raise exception -746, 0, "get_mthly_dept_rpt: Nonexistent department";
    end if;
    foreach select emp_id, emp_name, rate_per_hour
        into v_emp_id, v_emp_name, v_rate_per_hour
        from employee
        where dept_id = v_dept_id

        let v_hrs_worked = 0;
        let v_tot_hrs = 0;
        foreach select num_hours into v_hrs_worked
            from timecard
            where emp_id = v_emp_id
            and log_date between i_date_from and i_date_to

            let v_tot_hrs = v_tot_hrs + v_hrs_worked;

        end foreach;
        let v_tot_amt = v_tot_hrs * v_rate_per_hour;

        return i_dept_name, i_date_from, i_date_to, v_emp_id, v_emp_name,
            v_tot_hrs, v_rate_per_hour, v_tot_amt with resume;

    end foreach;
end
end procedure;
