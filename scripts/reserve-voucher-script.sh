#!/bin/bash
if [ $# -lt 2 ]
  then
    echo "VoucherType and transactionCode parameters are required!"
    exit 1
fi

#check input parameters
voucherType=$1
transactionCode=$2

#check headers and body
apiKey="5doxepApu54wyuZGGZCN2R" #stg
#apiKey="TfQpsC2YYFYJBcBiY6QNhx" #local
bodyNotEncoded="{ \"transactionId\": \"$transactionCode\"}"
bodyEncoded=$(printf '%s' "$bodyNotEncoded" | base64 | cut -f1 -d' ')
secretKey="b66ei9tv9p727ghwe049vv72351qi6rr7v24xh8e4h35te5krsx2o991jjh29s33"
before=$secretKey'.'$bodyEncoded
signatureKey="$(printf '%s' "$before" | sha256sum | cut -f1 -d' ')"
host="http://stg-vch-tnd.docomodigital.com"
#host="http://localhost:8085"

echo "Calling Reserve API with voucher-type $voucherType and transactionCode $transactionCode"
echo "Body Not Encoded: $bodyNotEncoded"
echo "Body Encoded: $bodyEncoded"
echo "X-Api-Key: $apiKey"
echo "X-Signature: $signatureKey"

echo curl -X POST \"$host/v1/external/voucher-type/$voucherType/reserve\" \
-H \"accept: application/json\" \
-H \"X-Signature: $signatureKey\" \
-H \"X-Api-Key: $apiKey\" \
-H \"Content-Type: application/json\" \
-d \"${bodyNotEncoded//\"/\\\"}\"
