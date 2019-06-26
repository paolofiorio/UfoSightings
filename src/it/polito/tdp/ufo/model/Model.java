package it.polito.tdp.ufo.model;

import java.time.Year;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.ufo.db.SightingsDAO;

public class Model {
	
	
	private SightingsDAO dao;
	public List<String> stati;
	private Graph<String, DefaultEdge> grafo;
	
	// PER LA RICORSIONE
	//1 struttura dati finale
	private List<String> ottima; //lista di stati in cui c'è lo stato di partenza e un insieme 
								// di archi e stati non ripetuti
	//2 struttura dati parziale
	//		lista definita nel metodo ricorsivo
	
	//3 condizione di terminazione
	//  	dato un determinato nodo, non ci sono più successori che non ho considerato
	
	
	//4 genero una nuova soluzione a partire da una parziale
	//		dato un ultimo nodo inserito nella sol parziale, costruisco una nuova soluzione 
	//  	considerando tutti i suoi successori
	
	//5 filtro
	//		ritornerò una sola soluzione ---> quella per cui la size() è massima
	
	//6 livello di ricorsione
	//		lunghezza del percorso parziale
	
	//7 caso iniziale
	//		parziale contiene il mio stato di partenza
	
	public  Model() {
		this.dao= new SightingsDAO();
	}
	
	public List<AnnoCount> getAnni(){
		
		return this.dao.getAnni();
		
	}

	public void creaGrafo(Year anno) {
		this.grafo = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		this.stati= this.dao.getStati(anno);
		
		Graphs.addAllVertices(this.grafo, this.stati);
		
		// sol semplice ; doppio ciclo, controllo esistenza arco
		
		for(String s1: this.grafo.vertexSet()) {
			for(String s2: this.grafo.vertexSet()) {
				if(!s1.equals(s2)) {
					if(this.dao.esisteArco(s1,s2,anno)) {
						this.grafo.addEdge(s1, s2);
					}
				}
		}
		
	}
		System.out.println("Grafo creato!");
		System.out.println("Num vertici : "+this.grafo.vertexSet().size());
		System.out.println("Num archi : "+this.grafo.edgeSet().size());
}

	public int getNVertici() {
		// TODO Auto-generated method stub
		return this.grafo.vertexSet().size();
	}

	public int getNArchi() {
		return this.grafo.edgeSet().size();
	}

	public List<String> getStati() {
		
		return this.stati;
	}
	public List<String> getSuccessori(String stato){
		return Graphs.successorListOf(this.grafo, stato);
	}
	public List<String> getPredecessori(String stato){
		return Graphs.predecessorListOf(this.grafo, stato);
		}
	public List<String> getRaggiungibili(String stato){
		List<String> raggiungibili = new LinkedList<String>();
		DepthFirstIterator<String, DefaultEdge>  dp= new DepthFirstIterator<String, DefaultEdge> (this.grafo);
		dp.next(); //scarta il primo elemento
		while(dp.hasNext()) {
			raggiungibili.add(dp.next());
		}
		return raggiungibili;
	}

	public List<String> getPercorsoMassimo(String partenza){
		
		this.ottima= new LinkedList<String>();
		List<String> parziale= new LinkedList<String>();
		
		//7
		parziale.add(partenza);
		
		cercaPercorsi(parziale);
		
		
		return this.ottima;
	}

	private void cercaPercorsi(List<String> parziale) {

		//3 testo la condizione di terminazione(vedere se la sol corrente è migliore della ottima)
		//5 con filtro
		
		if(parziale.size()>ottima.size())
			this.ottima = new LinkedList<String>(parziale);
		
		List<String> candidati = this.getSuccessori(parziale.get(parziale.size()-1));
	
		
		for(String candidato: candidati) {
			
			if(!parziale.contains(candidato)) {
				// è un candidato vero, non ancora considerato
				parziale.add(candidato);
				this.cercaPercorsi(parziale);
				//rimuovo l'ultimo elemento inserito nella lista
				parziale.remove(parziale.size()-1);
			}
			
		}
	}

}
