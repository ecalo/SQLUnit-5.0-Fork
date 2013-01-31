-- $Id: SQLUnit_TestMultipleOutputParams.sql,v 1.1 2005/06/03 01:02:46 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/mssqlserver/SQLUnit_TestMultipleOutputParams.sql,v $
--
if exists (select * 
           from dbo.sysobjects 
           where ((id = object_id(N'SQLUnit_TestMultipleOutputParams'))
             and  (OBJECTPROPERTY(id, N'IsProcedure') = 1))
          )
begin
    drop procedure SQLUnit_TestMultipleOutputParams
end
go


print 'Adding proc SQLUnit_TestMultipleOutputParams'
go


create procedure SQLUnit_TestMultipleOutputParams
--
--
-- Stored Proc: SQLUnit_TestMultipleOutputParams
--
-- Purpose:     Test the proper use of multiple output parameters within SQLUnit
--
-- Return Data:
--
--      Return Code:
--          @RC_SUCCESS, if successful
--          @RC_FAILURE, if unsuccessful
--  
(
    @param1     integer,
    @param2     integer,
    @param3     integer     output,
    @param4     integer     output
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


    --
    --  Provide a default value if NULL
    --
    set @param3 = 5 + isnull(@param1, 0)
    set @param4 = 10 + isnull(@param2, 0)


ExitProc:

    return (@ReturnCode)
    

    set nocount off      


end
go


