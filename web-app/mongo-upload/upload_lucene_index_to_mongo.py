from pymongo import MongoClient
from pprint import pprint
import json

mongodb_url="mongodb://127.0.0.1:27017/"
dbName = 'inverted_index'
collectionName = 'lucene_index'

client = MongoClient(mongodb_url)
db = client[dbName]
col = db[collectionName]

print(db)
print(col)


cats = ['suitcase', 'sports', 'baseball bat', 'remote', 'indoor', 'car', 'donut', 'carrot', 'traffic light', 'clock', 'teddy bear', 'cat', 'bottle', 'sheep', 'dog', 'kite', 'scissors', 'microwave', 'bowl', 'cell phone', 'backpack', 'train', 'airplane', 'bicycle', 'dining table', 'electronic', 'tie', 'banana', 'knife', 'apple', 'motorcycle', 'animal', 'toilet', 'bench', 'spoon', 'sandwich', 'couch', 'oven', 'orange', 'hair drier', 'tennis racket', 'tv', 'chair', 'book', 'food', 'wine glass', 'cow', 'potted plant', 'accessory', 'boat', 'parking meter', 'surfboard', 'toaster', 'bed', 'giraffe', 'vase', 'frisbee', 'hot dog', 'snowboard', 'horse', 'bird', 'mouse', 'skateboard', 'laptop', 'person', 'truck', 'baseball glove', 'refrigerator', 'kitchen', 'stop sign', 'elephant', 'cake', 'sports ball', 'outdoor', 'umbrella', 'zebra', 'fork', 'appliance', 'bus', 'furniture', 'skis', 'cup', 'vehicle', 'keyboard', 'bear', 'broccoli', 'pizza', 'sink', 'handbag', 'fire hydrant', 'toothbrush']

for c in cats:
    with open(f"lucene-results/{c}.txt", "r") as fin:
        data = fin.readlines()
        data = [d.replace("\n", "") for d in data]
        col.insert_one({"word": c, "images": data})
