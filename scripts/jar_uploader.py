#!/usr/bin/python3
import requests
import sys

data = open(sys.argv[1], 'rb').read()
#test get
res = requests.get(url='http://localhost:8080/api/hi')
print(res)
res = requests.post(url='http://localhost:8080/api/upload', data=data, headers={'Content-Type': 'application/octet-stream'})

print(res)
