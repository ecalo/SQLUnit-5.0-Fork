-- $Id: add_dept.sql,v 1.1 2004/07/09 03:12:44 dfishburn Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/asa/add_dept.sql,v $
-- $Revision: 1.1 $
-- Adding a new department to the database

IF EXISTS( SELECT 1
             FROM sys.sysprocedure sp KEY JOIN sys.sysuserperm sup
            WHERE sp.proc_name = 'add_dept'
              AND sup.user_name = user_name()                     ) THEN
    DROP PROCEDURE add_dept;
END IF
;
CREATE PROCEDURE add_dept(
    @dept_name varchar(64)
) 
RESULT( dept_id integer )   
BEGIN    
    SET TEMPORARY OPTION ON_TSQL_ERROR='Stop';

    IF EXISTS( SELECT 1
                 FROM department
                WHERE dept_name = @dept_name ) THEN
        RAISERROR 20746 'add_dept: Department already exists';
    END IF;

    INSERT INTO department( dept_name, num_employees )
    VALUES (@dept_name, 0);

    IF (SQLCODE < 0) THEN
        RAISERROR 20746 'add_dept: Insert into Department failed';
    END IF;

    SELECT @@IDENTITY;
END
;
