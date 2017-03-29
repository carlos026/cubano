package lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import util.Funcoes;

public class IndexFiles {

	private IndexWriter writer;
	private Path p;
	
	
//	Preparação da configuração para indexação
	public IndexFiles(String indexDirectoryPath) throws IOException {
		// Diretorio onde ficara os indices
		Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
		Funcoes f = new Funcoes();
		// Lista de palavras que serão desconsideradas ao ser feita a indexação
		// são usados ​​durante a ingestão, quando um documento é indexado e no momento da consulta. 
		// Um analisador examina o texto dos campos e gera um fluxo de token. Os analisadores podem ser uma única 
		// classe ou podem ser compostos de uma série de classes de tokenizador e filtro.
		ArrayList<String> stopWords = f.getStopWords();
		final CharArraySet stopSet = new CharArraySet(stopWords, false);
		// cria TokenStreams, que analisam o texto. Representa, portanto, uma política para extrair termos de índice do texto.
		Analyzer analyzer = new PortugueseAnalyzer(stopSet);
		// Constrói um novo IndexWriter de acordo com as configurações fornecidas em conf.
		//Armazena toda a configuração que é usado para criar uma IndexWriter. encadeia os tipos de configurações que serão realizadas no index
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		// O IndexWriter cria e mantém o índice, se já foi criado altera, se não cria.
		writer = new IndexWriter(indexDirectory, iwc);
		p = Paths.get(indexDirectoryPath);
		
	}
	
	
	// Indexação do documento
	private void indexFile(File file) throws IOException {
		System.out.println("Indexing " + file.getName());
		Document document = getDocument(file, p);
		if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
			writer.addDocument(document);
		} else {
			writer.updateDocument(new Term("filepath", file.toString()), document);
		}

	}

	// Verifia os arquivos a serem indexados e retorna o numero de arquivos indexados
	public int createIndex(String dataDirPath, FileFilter filter) throws IOException {
		// get all files in the data directory
		File[] files = new File(dataDirPath).listFiles();

		for (File file : files) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file);
			}
		}
		return writer.numDocs();
	}
	
	// Cria/compacta o documento e salva os dados a serem indexados
	private Document getDocument(File file, Path path) throws IOException {
   	    // é a unidade de pesquisa e índice, onde é armazenado os  pares campo-valor para identificar os dados indexados
		Document document = new Document();
		
		// Field são os campos a serem indexado com suas chaves/campo-valor
		// indexa o conteudo do arquivo
		Field contentField = new TextField("contents", new FileReader(file));
		// indexa o titulo/nome do arquivo
		Field fileNameField = new StringField("filename", file.getName(), Field.Store.YES);
		// indexa o caminho para a pasta do arquivo
		Field filePathField = new StringField("filepath", file.getCanonicalPath(), Field.Store.YES);
		// indexa a ultima vez que a pasta do arquivo foi modificada
		Field filemodified = new LongPoint("modified", Files.getLastModifiedTime(path).toMillis());

		document.add(contentField);
		document.add(fileNameField);
		document.add(filePathField);
		document.add(filemodified);

		return document;
	}
	
	// Finaliza a indexação
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}
}