-- $Id: parsertest_syb.sql,v 1.2 2005/07/09 01:39:20 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/java/parsertest_syb.sql,v $
--
if exists (select *  
from dbo.sysobjects  
where ((id = object_id(N'SQLUnit_IncludeTagTestProc')) 
and (OBJECTPROPERTY(id, N'IsProcedure') = 1)) 
) 
begin 
drop procedure SQLUnit_IncludeTagTestProc 
end 
go
 
create procedure SQLUnit_IncludeTagTestProc 
( 
@param1 integer output 
) 
as 
begin 
select @param1 
end 
go
