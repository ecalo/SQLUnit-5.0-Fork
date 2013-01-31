-- $Id: get_emp_by_name.sql,v 1.1 2004/07/09 03:12:44 dfishburn Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/asa/get_emp_by_name.sql,v $
-- $Revision: 1.1 $
-- Returns the employee record for the named employee

IF EXISTS( SELECT 1
             FROM sys.sysprocedure sp KEY JOIN sys.sysuserperm sup
            WHERE sp.proc_name = 'get_emp_by_name'
              AND sup.user_name = user_name()                     ) THEN
    DROP PROCEDURE get_emp_by_name;
END IF
;
CREATE PROCEDURE get_emp_by_name( 
    @emp_name varchar(64) 
) 
RESULT( 
    emp_id integer, 
    emp_name varchar(64), 
    dept_name varchar(64), 
    emp_dob date, 
    emp_ssn varchar(9), 
    rate_per_hour numeric(9,2) 
)
BEGIN
    DECLARE v_emp_name varchar(64);
    DECLARE v_emp_id integer;
    DECLARE v_dept_name varchar(64);
    DECLARE v_emp_dob date;
    DECLARE v_emp_ssn varchar(9);
    DECLARE v_rate_per_hour decimal(9,2);

    SET TEMPORARY OPTION ON_TSQL_ERROR='Stop';

    SELECT e.emp_id, e.emp_name, d.dept_name, e.emp_dob,
           e.emp_ssn, e.rate_per_hour
      INTO v_emp_id, v_emp_name, v_dept_name, v_emp_dob,
           v_emp_ssn, v_rate_per_hour
      FROM department d, employee e
     WHERE e.dept_id  = d.dept_id
       AND e.emp_name = @emp_name;

    IF (SQLCODE != 0) THEN
        RAISERROR 20746 'get_emp_by_name: Nonexistent employee';
    END IF;

    SELECT v_emp_id, v_emp_name, v_dept_name, date(v_emp_dob), v_emp_ssn, 
        v_rate_per_hour;
END
;
