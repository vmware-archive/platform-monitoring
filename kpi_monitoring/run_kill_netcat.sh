#!/usr/bin/env bash

ssh -o "StrictHostKeyChecking no" -i $OPSMANAGER_KEYPATH $OPSMANAGER_USERNAME@$OPSMANAGER_URL "sudo lsof -t -i tcp:12345 -s tcp:listen | sudo xargs kill"