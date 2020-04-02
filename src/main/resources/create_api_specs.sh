#!/bin/bash

mkdir -p ../../target/docs
cp voucher-api-v2.yaml ../../target/docs
cd ../../target/docs

node node_modules/redoc-cli/index.js bundle voucher-api-v2.yaml

#cp redoc-index.html ../../wwwdocs/index.html

