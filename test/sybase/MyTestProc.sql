if exists (
    select 1 
    from sysobjects
    where type="P"
    and name="MyTestProc"
)
drop procedure MyTestProc
go

create procedure MyTestProc
(
    @param1 numeric(13,2) OUTPUT
) as
begin
    if (@param1 is null)
    begin
        select @param1 = 5.25
    end

    select @param1 = @param1 + 0.01

    print 'param1=%1!', @param1

end
go
