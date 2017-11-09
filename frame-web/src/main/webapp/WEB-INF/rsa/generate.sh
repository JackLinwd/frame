#!/bin/bash

cd $1
mkdir $2
cd $2
openssl genrsa -out key.pem 2048
openssl req -new -key key.pem -out req.csr -subj /CN=cn/OU=ou/O=ou/CN=cn
openssl x509 -req -days 3650 -in req.csr -signkey key.pem -out cert.crt
openssl x509 -outform der -in cert.crt -out public.der
openssl rsa -in key.pem -out public.pem -pubout
openssl pkcs8 -topk8 -in key.pem -out private.pem -nocrypt
