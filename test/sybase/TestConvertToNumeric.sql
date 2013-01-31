if exists (
    select 1 
    from sysobjects 
    where type="P" 
    and name="TestConvertToNumeric" 
)
    drop procedure TestConvertToNumeric
go

/*
 * This procedure tests the behavior of Sybase with different kinds of
 * data types.
 */
create procedure TestConvertToNumeric (
    @chardata1 char(32),
    @intdata1 int output
) as
begin
    select @intdata1 = convert(int, @chardata1)
    return 0
end
go
