-- $Id: SQLUnit_TestDefaultParameterValues.sql,v 1.1 2005/06/03 01:02:46 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/mssqlserver/SQLUnit_TestDefaultParameterValues.sql,v $
--
if exists (select * 
           from dbo.sysobjects 
           where ((id = object_id(N'SQLUnit_TestDefaultParameterValues'))
             and  (OBJECTPROPERTY(id, N'IsProcedure') = 1))
          )
begin
    drop procedure SQLUnit_TestDefaultParameterValues
end
go


print 'Adding proc SQLUnit_TestDefaultParameterValues'
go


create procedure SQLUnit_TestDefaultParameterValues
--
--
-- Stored Proc: SQLUnit_TestDefaultParameterValues
--
-- Purpose:     Test the proper use of default parameter values using SQLUnit
--
-- Return Data:
--
--      Return Code:
--          @RC_SUCCESS, if successful
--          @RC_FAILURE, if unsuccessful
--  
(
    @alpha  integer=0,
    @beta   integer=1,
    @gamma  integer=2
)
as
begin
    set nocount on      -- Don't return number of rows affected 

    --
    --  Declare Constants
    --
    declare @RC_SUCCESS                     integer
    declare @RC_FAILURE                     integer

    --
    --  Initialize constants
    --
    set @RC_SUCCESS                     =   0
    set @RC_FAILURE                     =   -1

    --
    --  Declare Variables
    --
    declare @ReturnCode                 integer         -- Return code

    --
    --  Initialize variables
    --
    set @ReturnCode = @RC_SUCCESS


    select @alpha, @beta, @gamma



ExitProc:

    return (@ReturnCode)
    

    set nocount off      


end
go

