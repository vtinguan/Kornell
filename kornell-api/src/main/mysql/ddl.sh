#!/bin/sh

##cat ../mysql/kornell.sql
find ../ddl -name "*.sql" | sort | while read i

 do
   echo "-- $i"
   cat "$i"
 done
