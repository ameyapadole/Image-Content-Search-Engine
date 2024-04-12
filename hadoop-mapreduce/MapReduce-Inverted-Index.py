import json
import sys

# map reduce package import
from mrjob.job import MRJob
from mrjob.protocol import JSONProtocol

USAGE = '''
    python3 source_code.py input_data.json > output_file
'''

class Invertedindex(MRJob):
    def mapper(self, _, line):
        data = json.loads(line)
        
        id = data['id']
        category = data['category_id']
        supercategory = data['supercategory']
        
        yield(category,id)
        yield(supercategory,id)    

    def reducer(self, key, values):
        yield(key, list(values))

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("ERROR:", USAGE)
        exit(-1)

    INPUT_PROTOCOL = JSONProtocol
    Invertedindex().run()