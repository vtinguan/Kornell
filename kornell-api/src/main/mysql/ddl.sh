#!/bin/sh

cat ../mysql/kornell.sql
find . -name "*.sql" | sort | while read i

 do
   echo "-- $i"
   cat "$i"
 done