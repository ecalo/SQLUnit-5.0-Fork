#!/bin/sh
# $Id: reload.sh,v 1.3 2004/01/06 20:31:01 spal Exp $
# $Source: /cvsroot/sqlunit/sqlunit/test/postgresql/reload.sh,v $
# Create the schema and load the stored procedures
#
PSQL="psql -U defaultuser sqlunitdb"
cat schema.sql | $PSQL
for sp in `/bin/ls *sql | grep -v schema`; do
    echo "Uploading $sp"
    cat $sp | $PSQL
done
