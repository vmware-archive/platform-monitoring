#!/usr/bin/env bash

ssh -i $OPSMANAGER_KEYPATH $OPSMANAGER_USERNAME@$OPSMANAGER_URL 'bash -s' < assets/slow_netcat