java -Djavax.net.ssl.keyStore="client.keys" -Djavax.net.ssl.keyStorePassword="123456" \
-Djavax.net.ssl.trustStore="truststore" -Djavax.net.ssl.trustStorePassword="123456" \
SSLClient localhost 4444 REGISTER google.com 1.2.3.4
cmd /k