#!/usr/bin/env bash

# Create application user
echo -n "Creating order user"
sudo useradd -r -s /bin/false order
echo "... Done"
