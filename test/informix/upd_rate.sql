-- 
-- $Id: upd_rate.sql,v 1.1 2003/05/23 02:18:11 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/informix/upd_rate.sql,v $
-- Updates the rate for a given employee
--
--drop procedure upd_rate;
create procedure upd_rate(
    i_emp_name char(64), i_new_rate decimal(9,2)) returning
    integer,         -- status
    decimal(9,2);    -- old rate

define esql, eisam integer;
define emesg char(80);
define v_old_rate decimal(9,2);
define v_num_updated integer;

begin
    let v_old_rate = null;
    select rate_per_hour into v_old_rate 
        from employee
        where emp_name = i_emp_name;
    if (v_old_rate is null) then
        raise exception -746, 0, "upd_rate: Nonexistent employee";
    end if;
    update employee
        set rate_per_hour = i_new_rate
        where emp_name = i_emp_name;
    let v_num_updated = DBINFO('sqlca.sqlerrd2');
    if (v_num_updated != 1) then
        raise exception -746, 0, "upd_rate: Rate update failed";
    end if
    return 0, v_old_rate;
end
end procedure;
