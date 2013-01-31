-- $Id: upd_rate.sql,v 1.1 2004/07/09 03:12:44 dfishburn Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/asa/upd_rate.sql,v $
-- $Revision: 1.1 $
-- Updates the rate for a given employee

IF EXISTS( SELECT 1
             FROM sys.sysprocedure sp KEY JOIN sys.sysuserperm sup
            WHERE sp.proc_name = 'upd_rate'
              AND sup.user_name = user_name()                     ) THEN
    DROP PROCEDURE upd_rate;
END IF
;
CREATE PROCEDURE upd_rate(
    @emp_name varchar(64), 
    @new_rate decimal(9,2)
) 
BEGIN
    SET TEMPORARY OPTION ON_TSQL_ERROR='Stop';

    UPDATE employee
       SET rate_per_hour = @new_rate
     WHERE emp_name      = @emp_name;

    IF (SQLCODE = 100) THEN
        RAISERROR 20746 'upd_rate: Nonexistent employee';
    END IF;

END
;
