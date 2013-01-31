-- $Id: add_timecard.sql,v 1.1 2004/07/09 03:12:44 dfishburn Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/asa/add_timecard.sql,v $
-- $Revision: 1.1 $
-- Adds a timecard record for given employee

IF EXISTS( SELECT 1
             FROM sys.sysprocedure sp KEY JOIN sys.sysuserperm sup
            WHERE sp.proc_name = 'add_timecard'
              AND sup.user_name = user_name()                     ) THEN
    DROP PROCEDURE add_timecard;
END IF
;
CREATE PROCEDURE add_timecard(
    @emp_name varchar(64),
    @log_date datetime,
    @num_hours integer
)
RESULT( timecard_id integer )
BEGIN
    DECLARE v_emp_id integer;
    DECLARE v_timecard_id integer;
    DECLARE v_num_inserted integer;

    SET TEMPORARY OPTION ON_TSQL_ERROR='Stop';

    IF (@num_hours > 24) THEN
        RAISERROR 20746 'add_timecard: Too many hours, slow down';
    END IF;
    IF (@num_hours < 0) THEN
        RAISERROR 20746 'add_timecard: Too few hours, speed up';
    END IF;

    SET v_emp_id = NULL;

    SELECT emp_id
      INTO v_emp_id
      FROM employee
     WHERE emp_name = @emp_name;

    IF (SQLCODE != 0) THEN
        RAISERROR 20746 'add_timecard: Nonexistent employee';
    END IF;

    IF EXISTS( SELECT 1
                 FROM timecard
                WHERE emp_id   = v_emp_id
                  AND log_date = @log_date ) THEN
        RAISERROR 20746 'add_timecard: Timecard already exists';
    END IF;

    INSERT INTO timecard(emp_id, log_date, num_hours)
    VALUES ( v_emp_id, @log_date, @num_hours);

    SELECT @@IDENTITY;
END
;
