#!/usr/bin/env bash

ssh -i $OPSMANAGER_KEYPATH $OPSMANAGER_USERNAME@$OPSMANAGER_URL'bash -s' < `sudo lsof -t -i tcp:12345 -s tcp:listen | sudo xargs kill`