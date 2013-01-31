-- $Id: get_mthly_dept_rpt.sql,v 1.1 2004/07/09 03:12:44 dfishburn Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/asa/get_mthly_dept_rpt.sql,v $
-- $Revision: 1.1 $
-- Prints a monthly report for a given department

IF EXISTS( SELECT 1
             FROM sys.sysprocedure sp KEY JOIN sys.sysuserperm sup
            WHERE sp.proc_name = 'get_mthly_dept_rpt'
              AND sup.user_name = user_name()                     ) THEN
    DROP PROCEDURE get_mthly_dept_rpt;
END IF
;
CREATE PROCEDURE get_mthly_dept_rpt(
    @dept_name varchar(64), 
    @date_from date, 
    @date_to date
) 
RESULT( 
    dept_name varchar(64), 
    date_from date, 
    date_to date, 
    emp_id integer, 
    emp_name varchar(64),
    tot_hrs integer, 
    rate_per_hour numeric(9,2), 
    tot_amt numeric(11,2) 
) 
BEGIN
    DECLARE v_dept_id integer;
    DECLARE v_emp_id integer;
    DECLARE v_emp_name varchar(64);
    DECLARE v_rate_per_hour decimal(9,2);
    DECLARE v_hrs_worked integer;
    DECLARE v_tot_hrs integer;
    DECLARE v_tot_amt decimal(11,2);

    SET TEMPORARY OPTION ON_TSQL_ERROR='Stop';

    SET v_dept_id = NULL;

    SELECT dept_id
      INTO v_dept_id
      FROM department
     WHERE dept_name = @dept_name;

    IF (SQLCODE != 0) THEN
        RAISERROR 20746 'get_mthly_dept_rpt: Nonexistent department';
    END IF;


    SELECT d.dept_name, @date_from, @date_to
         , e.emp_id, e.emp_name
         , t.tot_hours
         , e.rate_per_hour
         , (e.rate_per_hour * t.tot_hours) AS tot_amt 
      FROM employee e JOIN department d ON (d.dept_id = e.dept_id) 
         , (SELECT t.emp_id, sum(t.num_hours) AS tot_hours
              FROM timecard t 
             WHERE t.log_date BETWEEN @date_from AND @date_to
             GROUP BY t.emp_id
           ) AS t
     WHERE d.dept_name = @dept_name
       AND e.emp_id    = t.emp_id
     GROUP BY d.dept_name
         , e.emp_id
         , e.emp_name
         , t.tot_hours
         , e.rate_per_hour
     ORDER BY d.dept_name
         , e.emp_id
         , e.emp_name
         , t.tot_hours
         , e.rate_per_hour

END
;
