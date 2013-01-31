if exists (select 1 from sysobjects where name='TestKeyRequest' and type='P')
    drop procedure TestKeyRequest
go
create procedure TestKeyRequest
    @keyVal char(3) output,
    @errcode int output,
    @errmsg varchar(100) output
as
begin
    select @keyVal = "ABC"
    select @errcode = 0
    select @errmsg = "success"

    return 0
end
go
