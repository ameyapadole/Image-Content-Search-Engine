//package indexer;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.*;
import java.util.List;

import org.apache.lucene.document.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.util.Version;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.DirectoryReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Indexer {
    public static void main(String[] args) throws IOException, ParseException, FileNotFoundException, org.json.simple.parser.ParseException {
        
        Analyzer analyzer = new StandardAnalyzer();
        
	//Store the index in memory:
        //Directory directory = new RAMDirectory();
        
	//To store an index on disk, use this instead:
	Directory directory = FSDirectory.open((new File("../../data/")).toPath());
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        JSONArray annotationList = readFile("export.json");

	int numIndexedDoc = 0;
	long startTime = System.currentTimeMillis();
        System.out.println("#Indexed Documnets: Elapsed time (ms)");
	
	for(Object annotation: (List) annotationList){
            Document doc = new Document();
	    
	    if(((JSONObject) annotation).get("id") != null && !((JSONObject) annotation).get("id").toString().isEmpty()){
                //System.out.println(String.valueOf(((JSONObject) annotation).get("id")));
                doc.add(new TextField("id", String.valueOf(((JSONObject) annotation).get("id")), Field.Store.YES));
            }
            
	    if(((JSONObject) annotation).get("image_id") != null && !((JSONObject) annotation).get("image_id").toString().isEmpty()){
                //System.out.println((String) (((JSONObject) annotation).get("image_id")));
		doc.add(new TextField("image_id", (String) (((JSONObject) annotation).get("image_id")), Field.Store.YES));
            }

            if(((JSONObject) annotation).get("category_id") != null && !((JSONObject) annotation).get("category_id").toString().isEmpty()){
                //System.out.println((String) (((JSONObject) annotation).get("category_id")));
                doc.add(new TextField("category_id", (String) (((JSONObject) annotation).get("category_id")), Field.Store.YES));
            }

            if(((JSONObject) annotation).get("supercategory") != null && !((JSONObject) annotation).get("supercategory").toString().isEmpty()){
                //System.out.println((String) (((JSONObject) annotation).get("supercategory")));
                doc.add(new TextField("supercategory", (String) (((JSONObject) annotation).get("supercategory")), Field.Store.YES));
            }

	    indexWriter.addDocument(doc);

	    numIndexedDoc = numIndexedDoc + 1;
	    if(numIndexedDoc % 500 == 0){
		long currentTime = System.currentTimeMillis();
        	System.out.println(numIndexedDoc +":" + (currentTime - startTime) +"(ms)");
	    }

        }
 
        long currentTime = System.currentTimeMillis();
        System.out.println(numIndexedDoc +":" + (currentTime - startTime) +"(ms)");

        indexWriter.close();

        //Now search the index:
        DirectoryReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser parser = new QueryParser("category_id", analyzer);
        Query query = parser.parse("tv person");
        
        System.out.println(query.toString());
        int topHitCount = 10;
        ScoreDoc[] hits = indexSearcher.search(query, topHitCount).scoreDocs;
        //Iterate through the results:
        for (int rank = 0; rank < hits.length; ++rank) {
            Document hitDoc = indexSearcher.doc(hits[rank].doc);
            System.out.println((rank + 1) + " (score:" + hits[rank].score + ")--> " + hitDoc.get("id") + ":" + hitDoc.get("supercategory"));
        }

        indexReader.close();

        directory.close();
    }

    private static JSONArray readFile(String filePath) throws FileNotFoundException, IOException, org.json.simple.parser.ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(filePath);
        Object file = jsonParser.parse(reader);
        JSONArray annotations = (JSONArray) file;

	return annotations;
    }

}
