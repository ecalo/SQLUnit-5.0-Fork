-- $Id: get_depts.sql,v 1.1 2004/07/09 03:12:44 dfishburn Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/asa/get_depts.sql,v $
-- $Revision: 1.1 $
-- Returns a list of all departments

IF EXISTS( SELECT 1
             FROM sys.sysprocedure sp KEY JOIN sys.sysuserperm sup
            WHERE sp.proc_name = 'get_depts'
              AND sup.user_name = user_name()                     ) THEN
    DROP PROCEDURE get_depts;
END IF
;
CREATE PROCEDURE get_depts(
    IN @dept_id integer DEFAULT NULL
) 
RESULT( 
    dept_id integer, 
    dept_name varchar(64)
)
BEGIN
    SET TEMPORARY OPTION ON_TSQL_ERROR='Stop';

    -- If @dept_id is NULL, this will return all deptartments.
    -- Otherwise, just the specified department will be returned.
    SELECT dept_id, dept_name
      FROM department
     WHERE dept_id = coalesce(@dept_id, dept_id)
     ORDER BY dept_name;
END
;
