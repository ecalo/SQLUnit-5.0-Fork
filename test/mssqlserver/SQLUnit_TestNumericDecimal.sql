-- $Id: SQLUnit_TestNumericDecimal.sql,v 1.1 2005/06/03 01:02:46 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/mssqlserver/SQLUnit_TestNumericDecimal.sql,v $
--
if exists (select * 
           from dbo.sysobjects 
           where ((id = object_id(N'SQLUnit_TestNumericDecimal'))
             and  (OBJECTPROPERTY(id, N'IsProcedure') = 1))
          )
begin
    drop procedure SQLUnit_TestNumericDecimal
end
go


print 'Adding proc SQLUnit_TestNumericDecimal'
go


create procedure SQLUnit_TestNumericDecimal
--
--
-- Stored Proc: SQLUnit_TestNumericDecimal
--
-- Purpose:     Test the proper use of NUMERIC and DECIMAL data types within SQLUnit
--
-- Return Data:
--
--      Return Code:
--          @RC_SUCCESS, if successful
--          @RC_FAILURE, if unsuccessful
--  
(
    @param1     numeric(13,2)   output
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
    if (@param1 is NULL)
    begin
        set @param1 = 5.25
    end
    
    --
    --  Add 0.01 to the parameter.  
    --
    set @param1 = @param1 + 0.01

    --
    --  Create the result set
    --
    select @param1

ExitProc:

    return (@ReturnCode)
    

    set nocount off      


end
go


