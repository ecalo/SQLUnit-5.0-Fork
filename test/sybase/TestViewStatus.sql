if exists (
    select 1 
    from sysobjects
    where type="P"
    and name="TestViewStatus"
)
    drop procedure TestViewStatus
go

/*
 * This tests the return value of numeric problem
 */
create procedure TestViewStatus (
    @chardata1 char(32),
    @errorCode int out,
    @errorDesc varchar(256) out
) as begin
    -- outparams
    select @errorCode = 0
    select @errorDesc = "success"
    declare
        @numcol1 numeric,
        @numcol2 numeric,
        @charcol1 char(2),
        @vcharcol1 varchar(256),
        @vcharcol2 varchar(256),
        @charcol2 char(11),
        @vcharcol3 varchar(256)

    -- resultset 1
    select @numcol1 = 35222643
    select @numcol2 = 118
    select @charcol1 = "IN"
    select @vcharcol1 = "Compliments"
    select @vcharcol2 = "0 "
    select @charcol2 = "01 Oct 2003"
    select @vcharcol3 = "ML"
    select @numcol1, @numcol2, @charcol1, @vcharcol1, @vcharcol2, 
        @charcol2, @vcharcol3
    return 0
end
go
