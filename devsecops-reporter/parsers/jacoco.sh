#!/bin/bash
FILE=$1

MISSED=$(xmllint --xpath "string(//counter[@type='LINE']/@missed)" $FILE)
COVERED=$(xmllint --xpath "string(//counter[@type='LINE']/@covered)" $FILE)
TOTAL=$((MISSED + COVERED))
COVERAGE=$((100 * COVERED / TOTAL))

STATUS="PASS"
[ "$COVERAGE" -lt 70 ] && STATUS="FAIL"

cat <<EOF
{"tool":"JaCoCo","type":"COVERAGE","coverage":$COVERAGE,"status":"$STATUS"}
EOF
