#!/bin/sh

uaac set target http://localhost:8081/uaa
echo "adminsecret"|uaac token client get admin
uaac user delete mr.pressident
uaac user delete littlegirl
