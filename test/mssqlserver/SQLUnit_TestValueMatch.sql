-- $Id: SQLUnit_TestValueMatch.sql,v 1.1 2005/06/03 01:02:46 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/mssqlserver/SQLUnit_TestValueMatch.sql,v $
--
if exists (select * 
           from dbo.sysobjects 
           where ((id = object_id(N'dbo.SQLUnit_TestValueMatch'))
             and  (OBJECTPROPERTY(id, N'IsScalarFunction') = 1))
          )
begin
    drop function dbo.SQLUnit_TestValueMatch
end
go


print 'Adding function dbo.SQLUnit_TestValueMatch'
go


create function dbo.SQLUnit_TestValueMatch
--
--
-- Stored Proc: SQLUnit_TestValueMatch
--
-- Purpose:     Test used to match values
--
-- Return Code:
--      @RC_TRUE,  if values match
--      @RC_FALSE, if values do not match
--  
(
    @param1     integer,
    @param2     integer
)
returns integer
as
begin

    --
    --  Declare Constants
    --
    declare @RC_FALSE          integer
    declare @RC_TRUE           integer        

    --
    --  Initialize constants
    --
    set @RC_FALSE                     =   0
    set @RC_TRUE                      =   1

    --
    --  Declare Variables
    --
    declare @ReturnCode                 integer         -- Return code


    --
    --  If you get behavior you did not expect, check the ANSI_NULLS
    --  setting within your database
    --
    if (@param1 = @param2)
        begin
            set @ReturnCode = @RC_TRUE
        end
    else
        begin
            set @ReturnCode = @RC_FALSE
        end


ExitFunc:

    return (@ReturnCode)
    


end
go
