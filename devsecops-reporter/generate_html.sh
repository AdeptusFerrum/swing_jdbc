#!/bin/bash

echo "<html><body><h1>DevSecOps Report</h1><table border='1'>" > report.html
echo "<tr><th>Tool</th><th>Type</th><th>Status</th></tr>" >> report.html

jq -c '.[]' summary.json | while read row; do
  TOOL=$(echo $row | jq -r '.tool')
  TYPE=$(echo $row | jq -r '.type')
  STATUS=$(echo $row | jq -r '.status')
  echo "<tr><td>$TOOL</td><td>$TYPE</td><td>$STATUS</td></tr>" >> report.html
done

echo "</table></body></html>" >> report.html
