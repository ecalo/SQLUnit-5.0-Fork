-- $Id: get_emp_in_dept.sql,v 1.1 2004/07/09 03:12:44 dfishburn Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/asa/get_emp_in_dept.sql,v $
-- $Revision: 1.1 $
-- Returns all employees for a given department

IF EXISTS( SELECT 1
             FROM sys.sysprocedure sp KEY JOIN sys.sysuserperm sup
            WHERE sp.proc_name = 'get_emp_in_dept'
              AND sup.user_name = user_name()                     ) THEN
    DROP PROCEDURE get_emp_in_dept;
END IF
;
CREATE PROCEDURE get_emp_in_dept(
    @dept_name varchar(64)
) 
RESULT( 
    dept_id integer, 
    emp_id integer, 
    emp_name varchar(64), 
    emp_dob date, 
    emp_ssn varchar(9), 
    rate_per_hour numeric(6,2) 
)
BEGIN
    DECLARE @dept_id integer;

    SET TEMPORARY OPTION ON_TSQL_ERROR='Stop';

    SET @dept_id = NULL;

    SELECT dept_id
      INTO @dept_id
      FROM department
     WHERE dept_name = @dept_name;

    IF (SQLCODE != 0) THEN
        RAISERROR 20746, 0, "get_emp_in_dept: Nonexistent Department";
    END IF;

    SELECT dept_id, emp_id, emp_name, emp_dob, emp_ssn, rate_per_hour
      FROM employee
     WHERE dept_id = @dept_id
     ORDER BY emp_id;

END
;
