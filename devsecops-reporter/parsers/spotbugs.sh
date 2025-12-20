#!/bin/bash
FILE=$1

if [ ! -f "$FILE" ]; then
  echo '{"tool":"SpotBugs","type":"SAST","issues":0,"status":"SKIPPED"}'
  exit 0
fi

BUGS=$(xmllint --xpath "count(//BugInstance)" "$FILE" 2>/dev/null || echo 0)
BUGS=${BUGS:-0}

STATUS="PASS"
[ "$BUGS" -gt 0 ] && STATUS="FAIL"

echo "{\"tool\":\"SpotBugs\",\"type\":\"SAST\",\"issues\":$BUGS,\"status\":\"$STATUS\"}"
