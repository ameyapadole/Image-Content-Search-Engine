//package searcher;

import java.io.IOException;
import java.io.*;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Iterator;

import org.apache.lucene.document.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.util.Version;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.DirectoryReader;

public class Searcher {

    Directory directory;
    DirectoryReader indexReader;
    IndexSearcher indexSearcher;
    MultiFieldQueryParser queryParser;
    Query query;
    
    private static  String[] fields = {"category_id", "supercategory"};
    private static int topHitCount = 250;


    public Searcher() throws IOException{
        this.directory = FSDirectory.open((new File("../../data/")).toPath());
        this.indexReader = DirectoryReader.open(directory);
        this.indexSearcher = new IndexSearcher(indexReader);
        this.queryParser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
    }

    public Document[] search( String searchQuery) throws IOException, ParseException {

        long startTime = System.currentTimeMillis();
        //System.out.println("#Search in Documnets: Elapsed time (ms)");

        this.query = queryParser.parse(searchQuery);
        //System.out.println(query.toString());
        
        ScoreDoc[] hits = indexSearcher.search(query, topHitCount).scoreDocs;
        Document[] results = new Document[topHitCount];
	ArrayList<String> image_ids = new ArrayList<String>();

        //Iterate through the results:
        for (int rank = 0; rank < hits.length; ++rank) {
            Document hitDoc = indexSearcher.doc(hits[rank].doc);
            //System.out.println((rank + 1) + " (score:" + hits[rank].score + ")--> " + hitDoc.get("id") + ":" + hitDoc.get("image_id"));
            results[rank] = hitDoc;
	    image_ids.add(hitDoc.get("image_id"));
        }

	LinkedHashSet<String> unique_image_ids = new LinkedHashSet<String>(image_ids);
	/*int count = 50;
	for(String image_id: unique_image_ids){
	    System.out.println(image_id);
	    count--;
	    if(count < 1) break;
	}*/

	export_data_into_json(searchQuery, unique_image_ids);

        long currentTime = System.currentTimeMillis();
        //System.out.println((currentTime - startTime) +"(ms)");

        return results;
    }

    public void close() throws IOException{
        this.indexReader.close();
        this.directory.close();
    }

    public void export_data_into_json(String keyword, LinkedHashSet<String> image_ids) throws IOException {
	BufferedWriter output = new BufferedWriter(new FileWriter("results/" + keyword + ".json"));
        Iterator it = image_ids.iterator();
	int count = 50;
	while(it.hasNext() && count > 0){
	    output.write(it.next().toString());
	    output.newLine();
	    count--;
	}
	output.close();
    }

    public static void main(String[] args) throws IOException, ParseException {
	
	String inputQuery = args[0];
	//System.out.println(inputQuery);

	Searcher luceneSearcher = new Searcher();
	luceneSearcher.search(inputQuery);
	luceneSearcher.close();
    }

}
