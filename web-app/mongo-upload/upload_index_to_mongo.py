from pymongo import MongoClient
from pprint import pprint
import json

mongodb_url="mongodb://127.0.0.1:27017/"
dbName = 'inverted_index'
collectionName = 'index'

client = MongoClient(mongodb_url)
db = client[dbName]
col = db[collectionName]

print(db)
print(col)

data = None
with open("inverted-index-whole-words", "r") as fin:
    data = fin.readlines()

for rec in data:
    word, ids = rec.split("\t")
    word = word[1:-1]
    ids = json.loads(ids)
    col.insert_one({'word': word, 'ids': ids})
    
client.close();