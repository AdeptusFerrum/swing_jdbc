#!/bin/bash

RESULTS=()

RESULTS+=("$(bash parsers/spotbugs.sh reports/spotbugsXml.xml)")
RESULTS+=("$(bash parsers/jacoco.sh reports/jacoco.xml)")

printf '%s\n' "${RESULTS[@]}" | jq -s '.' > summary.json
