#!/usr/bin/python3
import requests
import sys

data = open(sys.argv[1], 'rb').read()
res = requests.post(url='http://192.168.2.22:8080/api/upload', data=data, headers={'Content-Type': 'application/octet-stream'})
print(res)
