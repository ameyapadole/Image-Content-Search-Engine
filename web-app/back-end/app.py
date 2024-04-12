import time
import logging
from collections import defaultdict

#flask libraries
from flask import Flask, render_template, request, redirect
from flask_cors import CORS, cross_origin

# mongo library
from pymongo import MongoClient


logging.basicConfig(format='%(asctime)s | %(levelname)s | %(message)s', datefmt="%Y-%m-%d %H:%M:%S", level=logging.DEBUG)

# start flask app
app = Flask(__name__)
cors = CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'

# mongo connection parameters
mongodb_url="mongodb://127.0.0.1:27017/"
dbName = 'inverted_index'

# establish mongo connection
client = MongoClient(mongodb_url)
db = client[dbName]
indexCol = db['index']
annCol = db['annotations']
luceneCol = db['lucene_index']

# route to get the possible categories in the index
@app.route("/categories" ,methods=["GET"])
@cross_origin()
def getCategories():
    docs = list(indexCol.find({}))
    if docs == None:
        return {"error" : "index collection could not be accessed"}, 500
    categories = set(rec['word'] for rec in docs)
    return {'categories': list(categories)}


# route to run the search query
@app.route("/search" ,methods=["POST"])
@cross_origin()
def search_query_responder():
    start = time.time()
    form = request.form
    query = form['query']
    mode = form['mode']

    if mode == 'hadoop':
        ids = runInvertedIndexQuery(query)
        if ids == None:
            return {"error" : "query word not found in inverted index"}, 500

        imgs = getImagesById(ids, query)
        end = time.time()
        
        logging.debug(f"Inverted Index with ranking time: {end-start}")
        
        return {'images':imgs}

    elif mode == 'lucene':
        images = runLuceneQuery(query)
        end = time.time()
        
        logging.debug(f"Lucene without additional ranking time: {end-start}")
        
        return {'images': images}
    else:
        return {"error" :f"mode: {mode} not supported"}, 500
       
def runInvertedIndexQuery(query):
    start = time.time()
    rec = indexCol.find_one({'word': query})
    if rec == None:
        return None
    ids = rec['ids']
    end = time.time()

    logging.debug(f"Inverted Index without ranking time: {end-start}")
    return ids

def getImagesById(ids, query):
    all_annotations = defaultdict(list)

    for id in ids:
        annotation = annCol.find_one({'id': id})
        all_annotations[annotation['image_id']].append(annotation)
        # unique_images.add(annotation['image_id'])

    images = rankImages(all_annotations)

    return list(images)

def rankImages(all_ann):
    areas = []
    for img in all_ann:
        rec = {'image_id': img}
        area_ratio_sum = 0.0
        for ann in all_ann[img]:
            area_ratio_sum += ann['area_ratio']
        
        rec['area_ratio'] = area_ratio_sum
        areas.append(rec)
            
    areas = sorted(areas, key=lambda x: x['area_ratio'], reverse=True)

    return [d['image_id'] for d in areas]

def runLuceneQuery(query):
    rec = luceneCol.find_one({"word": query})
    if rec == None:
        return []
    return rec['images']
    
if __name__ == "__main__":
    app.run(debug=True, threaded=True, host='0.0.0.0', port="5555")