#!/usr/bin/env python3
import requests
import sys

if(len(sys.argv) < 3):
    print('usage: jar_uploader.py IP_OF_CLUSTER PATH_TO_JAR')
    exit(1)

data = open(sys.argv[2], 'rb').read()
res = requests.post(url='http://%s:8080/api/upload' % sys.argv[1], data=data, headers={'Content-Type': 'application/octet-stream'})
print(res)
