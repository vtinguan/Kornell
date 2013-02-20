url=http://jf-mb.local:8080/api/jpql
auth='fulano:detal'
query=$1

curl -v -G -u $auth --data-urlencode "q=$query" $url
