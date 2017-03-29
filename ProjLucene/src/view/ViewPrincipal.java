package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import lucene.IndexFiles;
import lucene.SearchFiles;
import lucene.TextFileFilter;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;
import java.awt.Desktop;

import javax.swing.JTable;
import javax.swing.JScrollPane;

public class ViewPrincipal extends JFrame {

	private JPanel contentPane;
	private JTextField tfTexto;

	//String dataDir = "/Users/Lory1/Documents/Proj_Faculdade/Lucene/text";
	String dataDir = "/Users/Carlos/Documents/Fucapi/Topicos de banco de dados/teste";
	lucene.IndexFiles indexer;
	lucene.SearchFiles searcher;
	private JTable table;
	
	static ArrayList<String> urls;
	// JPanel panel;
	// private DefaultTableModel modelo = new DefaultTableModel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewPrincipal frame = new ViewPrincipal();
					frame.setVisible(true);
					urls = new ArrayList<>();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ViewPrincipal() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		tfTexto = new JTextField();
		tfTexto.setBounds(6, 92, 305, 26);
		contentPane.add(tfTexto);
		tfTexto.setColumns(10);
		
		JLabel lblDigiteOQue = new JLabel("Digite o que deseja pesquisar:");
		lblDigiteOQue.setBounds(6, 78, 222, 16);
		contentPane.add(lblDigiteOQue);
		
		JLabel lblDetalhes = new JLabel("");
		lblDetalhes.setForeground(Color.RED);
		lblDetalhes.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblDetalhes.setBounds(6, 116, 418, 16);
		contentPane.add(lblDetalhes);
		
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 130, 438, 142);
		contentPane.add(scrollPane);
		
		DefaultTableModel modelo = new DefaultTableModel();
		JTable table = new JTable(modelo);
		table.setModel(new DefaultTableModel(
				new  Object [][]{
					
				},
				new String[] {
						"Titulo"
				}));
		
		table.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
            	int row = table.getSelectedRow();
            	URI uri;
				try {
					uri = new URI(urls.get(row));
					open(uri);
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
});
		

		
		scrollPane.setViewportView(table);
		
		JButton btnPesquisar = new JButton("Pesquisar");
		btnPesquisar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ViewPrincipal vp;
				try {
					vp = new ViewPrincipal();
					vp.createIndex();									
					vp.search(tfTexto.getText(), lblDetalhes, table);
				} catch (IOException exception) {
					exception.printStackTrace();
			      }
			         catch (ParseException exception) {
			        	 exception.printStackTrace();
			      }
			}
		});
		btnPesquisar.setBounds(311, 92, 117, 29);
		contentPane.add(btnPesquisar);
		
		JLabel status = new JLabel(); 
		//status.setIcon(new ImageIcon("img/img_tcc.png")); 
		
		JPanel panel = new JPanel();
		panel.setBounds(311, 6, 117, 74);
		panel.add(status);
		contentPane.add(panel);
		
		
	}

	/**
	 * Funções.
	 */
	
	private void open(URI uri) {
		  if (Desktop.isDesktopSupported()) {
		    try {
		       Desktop.getDesktop().browse(uri);
		      } catch (IOException e) { /* TODO: error handling */ }
		   } else { /* TODO: error handling */ }
		 }

	private void createIndex() throws IOException {
		indexer = new IndexFiles(dataDir);
		int numIndexed;
		long startTime = System.currentTimeMillis();
		numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
		long endTime = System.currentTimeMillis();
		indexer.close();
		System.out.println(numIndexed + " arquivos indexados, em: " + (endTime - startTime) + " ms");
	}

	private void search(String searchQuery, JLabel lblDetalhes, JTable table) throws IOException, ParseException {
		int numCols = table.getModel().getColumnCount();
		((DefaultTableModel) table.getModel()).setNumRows(0);

		searcher = new SearchFiles(dataDir);
		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.search(searchQuery);
		long endTime = System.currentTimeMillis();

		System.out.println(
				" Documentos encontrados: " + hits.totalHits + ". Tempo de resposta: " + (endTime - startTime) + "ms");
		if (hits.totalHits == 0) {
			lblDetalhes.setText("Nenhum Documento encontrado. Tempo de resposta: " + (endTime - startTime));
		} else {
			lblDetalhes.setText("Documentos encontrados: " + hits.totalHits + ". Tempo de resposta: "
					+ (endTime - startTime) + "ms");

		}
		
		

		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.getDocument(scoreDoc);
			System.out.println("File: " + doc.get("filepath"));
			if (hits.totalHits > 0) {
				Object[] fila = new Object[numCols];
				fila[0] = doc.get("filename");
				urls.add(doc.get("filepath"));
				
				((DefaultTableModel) table.getModel()).addRow(fila);
			}
		}

	}
}
