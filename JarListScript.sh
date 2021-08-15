#!/bin/sh

> list
for x in *.jar; do
  echo $x >> list
done