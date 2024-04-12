from pymongo import MongoClient
from pprint import pprint
import json

mongodb_url="mongodb://127.0.0.1:27017/"
dbName = 'inverted_index'
collectionName = 'annotations'

client = MongoClient(mongodb_url)
db = client[dbName]
col = db[collectionName]

print(db)
print(col)

data = None
with open("annotations-individual-records-2.json", "r") as fin:
    data = fin.readlines()

for rec in data:
    rec = json.loads(rec)
    col.insert_one(rec)
    
client.close();