#!/usr/bin/env bash

scp -o "StrictHostKeyChecking no" -i $OPSMANAGER_KEYPATH assets/slow_netcat $OPSMANAGER_USERNAME@$OPSMANAGER_URL:/home/$OPSMANAGER_USERNAME/
ssh -o "StrictHostKeyChecking no" -i $OPSMANAGER_KEYPATH $OPSMANAGER_USERNAME@$OPSMANAGER_URL 'bash -s' < "./slow_netcat"