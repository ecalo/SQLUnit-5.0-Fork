-- $Id: add_employee.sql,v 1.1 2004/07/09 03:12:44 dfishburn Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/asa/add_employee.sql,v $
-- $Revision: 1.1 $
-- Adding a new employee to the database

IF EXISTS( SELECT 1
             FROM sys.sysprocedure sp KEY JOIN sys.sysuserperm sup
            WHERE sp.proc_name = 'add_employee'
              AND sup.user_name = user_name()                     ) THEN
    DROP PROCEDURE add_employee;
END IF
;
CREATE PROCEDURE add_employee(
    @dept_name varchar(64), 
    @emp_name varchar(64),
    @emp_dob date,
    @emp_ssn varchar(9),
    @emp_payrate numeric(9,2)
)
RESULT( emp_id integer )
BEGIN
    DECLARE v_dept_id integer;

    SET TEMPORARY OPTION ON_TSQL_ERROR='Stop';

    SET v_dept_id = NULL;

    SELECT dept_id
      INTO v_dept_id
      FROM department
     WHERE dept_name = @dept_name;

    IF (SQLCODE != 0) THEN
        RAISERROR 20746 'add_employee: Non-existent department';
    END IF;

    IF EXISTS( SELECT 1
                 FROM employee
                WHERE emp_name = @emp_name ) THEN
        RAISERROR 20746 'add_employee: Employee already exists';
    END IF;

    INSERT INTO employee( dept_id, emp_name, emp_dob, emp_ssn, rate_per_hour)
    VALUES (v_dept_id, @emp_name, @emp_dob, @emp_ssn, @emp_payrate);

    SELECT @@IDENTITY ;
END
;
