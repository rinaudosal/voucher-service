#!/bin/bash
#examplerequest: ./ TNDIDSP1MONTH 8673be7a-33b2-4822-a0d0-41bba9db0117

if [ $# -lt 2 ]
  then
    echo "VoucherType and voucherCode parameters are required!"
    exit 1
fi

#check input parameters
voucherType=$1
voucherCode=$2

#check headers and body
apiKey="5doxepApu54wyuZGGZCN2R" #stg
#apiKey="TfQpsC2YYFYJBcBiY6QNhx" #local
bodyEncoded=$(printf '%s' "" | base64 -w 0 | cut -f1 -d' ')
secretKey="b66ei9tv9p727ghwe049vv72351qi6rr7v24xh8e4h35te5krsx2o991jjh29s33"
before=$secretKey'.'$bodyEncoded
signatureKey="$(printf '%s' "$before" | sha256sum | cut -f1 -d' ')"
host="http://stg-vch-tnd.docomodigital.com"
#host="http://localhost:8085"

echo "Calling Get voucher API with voucher type $voucherType and voucher code $voucherCode"
echo "Body Encoded: $bodyEncoded"
echo "X-Api-Key: $apiKey"
echo "X-Signature: $signatureKey"

echo curl -X GET \"$host/v1/external/voucher-type/$voucherType/voucher/$voucherCode\" \
-H \"accept: application/json\" \
-H \"X-Signature: $signatureKey\" \
-H \"X-Api-Key: $apiKey\" \
-H \"Content-Type: application/json\" \
