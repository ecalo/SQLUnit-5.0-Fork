-- $Id: PassThru.sql,v 1.1 2004/02/03 06:37:50 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/postgresql/PassThru.sql,v $
-- Returns the parameter passed in, for testing.
--
create or replace function PassThru(varchar) returns varchar as '
  declare 
    i_param alias for $1;
  begin
    return i_param;
  end;
' language 'plpgsql';
