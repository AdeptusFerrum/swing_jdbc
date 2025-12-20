#!/bin/bash
FILE=$1

if [ ! -f "$FILE" ]; then
  echo '{"tool":"JaCoCo","type":"COVERAGE","coverage":0,"status":"SKIPPED"}'
  exit 0
fi

MISSED=$(xmllint --xpath "string(//counter[@type='LINE']/@missed)" "$FILE" 2>/dev/null)
COVERED=$(xmllint --xpath "string(//counter[@type='LINE']/@covered)" "$FILE" 2>/dev/null)

MISSED=${MISSED:-0}
COVERED=${COVERED:-0}
TOTAL=$((MISSED + COVERED))

if [ "$TOTAL" -eq 0 ]; then
  COVERAGE=0
else
  COVERAGE=$((100 * COVERED / TOTAL))
fi

STATUS="PASS"
[ "$COVERAGE" -lt 70 ] && STATUS="FAIL"

echo "{\"tool\":\"JaCoCo\",\"type\":\"COVERAGE\",\"coverage\":$COVERAGE,\"status\":\"$STATUS\"}"
