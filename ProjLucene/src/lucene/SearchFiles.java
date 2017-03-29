package lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import util.Funcoes;

public class SearchFiles {
	
	IndexSearcher indexSearcher;
	QueryParser queryParser;
	Query query;

	public SearchFiles(String indexDirectoryPath) throws IOException {
		Path path = Paths.get(indexDirectoryPath);
		Directory directory = FSDirectory.open(path);
		IndexReader indexReader = DirectoryReader.open(directory);
		indexSearcher = new IndexSearcher(indexReader);
		Funcoes f = new Funcoes();
		ArrayList<String> stopWords = f.getStopWords();
		final CharArraySet stopSet = new CharArraySet(stopWords, false);
		Analyzer analyzer = new PortugueseAnalyzer(stopSet);
		queryParser = new QueryParser("contents", analyzer);
	}



	public TopDocs search(String searchQuery) throws IOException, ParseException {
		query = queryParser.parse(searchQuery);
		return indexSearcher.search(query, 10);
	}

	public TopDocs search(Query query) throws IOException, ParseException {
		return indexSearcher.search(query, 10);
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}
}
