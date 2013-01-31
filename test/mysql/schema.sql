-- $Id: schema.sql,v 1.5 2004/09/08 01:06:58 spal Exp $
-- $Source: /cvsroot/sqlunit/sqlunit/test/mysql/schema.sql,v $
-- Description: Schema for a simple test database with one table, only
-- to enable some quick and dirty debugging. I used this table from another
-- project - it has no particular relevance in the context of SQLUnit.
--
create table customer (
  custId integer not null,
  custname varchar(255) not null,
  custpass varchar(255) not null,
  langpref varchar(5) default 'en_US' not null,
  defaultapp varchar(36),
  signedon integer default 0 not null,
  lastsignedon datetime
);

-- This is for testing all LOB functionality
create table lobtest ( user varchar(32) not null, permission blob, securekey mediumblob, certificate text);

-- This is for testing diff functionality with and without matchers
create table widgets (
  widget_id integer not null,
  widget_name varchar(255),
  price_per_unit float,
  number_sold integer
);
