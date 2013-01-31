if exists (select 1 from sysobjects where name='TestNullOutParam' and type='P')
    drop procedure TestNullOutParam
go
create procedure TestNullOutParam
    @errcode int output,
    @errmsg varchar(100) output
as
begin
    select @errcode = 10
end
go
