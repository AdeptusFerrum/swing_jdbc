#!/bin/bash

BOT_TOKEN="$BOT_TOKEN"
CHAT_ID="$CHAT_ID"

TEXT="DevSecOps Report:%0A$(jq -r '.[] | "\(.tool): \(.status)"' summary.json)"

curl -s -X POST "https://api.telegram.org/bot$BOT_TOKEN/sendMessage" \
  -d chat_id="$CHAT_ID" \
  -d text="$TEXT"
