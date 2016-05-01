#!/bin/bash

function suicide() {
	echo $1 >&2
	exit
}

function log() {
  echo "$1"
}

function demmand() {
  local value="${!1}"
  
  if [ -z "$value" ]; 
    then suicide "Environment variable $1 is demmanded"; 
    else echo "$1 = $value"
  fi  
}