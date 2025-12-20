#!/bin/bash
FILE=$1

BUGS=$(xmllint --xpath "count(//BugInstance)" $FILE)

STATUS="PASS"
[ "$BUGS" -gt 0 ] && STATUS="FAIL"

cat <<EOF
{"tool":"SpotBugs","type":"SAST","issues":$BUGS,"status":"$STATUS"}
EOF
