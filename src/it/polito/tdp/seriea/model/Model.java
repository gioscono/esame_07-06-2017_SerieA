package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	private SerieADAO dao;
	private List<Team> squadre;
	private SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo;
	private Map<Team, Integer> mappaPunti;
	
	public Model(){
		dao = new SerieADAO();
	}
	
	public List<Season> getStagioni(){
		return dao.listSeasons();
	}

	public List<Team> getAllSquadre(Season s){
		squadre = dao.listTeamsStagione(s);
		mappaPunti = new HashMap<>();
		for(Team t : squadre){
			mappaPunti.put(t, 0);
		}
		return squadre;
	}
	
	
	public List<TeamAndPoints> creaGrafo(Season s){
		
		grafo = new SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		System.out.println(grafo.vertexSet().size()+" "+grafo.edgeSet().size());
		Graphs.addAllVertices(grafo, this.getAllSquadre(s));
		
		for(Team casa : grafo.vertexSet()){
			for(Team ospite : grafo.vertexSet()){
				if(!casa.equals(ospite)){
					Match partita = dao.getMatch(casa, ospite, s);
					if(partita.getFthg()>partita.getFtag()){
						Graphs.addEdgeWithVertices(grafo, casa, ospite, +1.0);
					}else{
						if(partita.getFthg()<partita.getFtag()){
							Graphs.addEdgeWithVertices(grafo, casa, ospite, -1.0);
						}else{
							Graphs.addEdgeWithVertices(grafo, casa, ospite, +0.0);
						}
					}
				}
			}
		}
		System.out.println(grafo);
		System.out.println(grafo.vertexSet().size()+" "+grafo.edgeSet().size());
		
		for(DefaultWeightedEdge partita : grafo.edgeSet()){
			
			if(grafo.getEdgeWeight(partita)==1.0){
				
				mappaPunti.replace(grafo.getEdgeSource(partita), mappaPunti.get(grafo.getEdgeSource(partita)), mappaPunti.get(grafo.getEdgeSource(partita))+3);
			}else{
				if(grafo.getEdgeWeight(partita)==-1.0){
					mappaPunti.replace(grafo.getEdgeTarget(partita), mappaPunti.get(grafo.getEdgeTarget(partita)), mappaPunti.get(grafo.getEdgeTarget(partita))+3);
				}else{
					mappaPunti.replace(grafo.getEdgeSource(partita), mappaPunti.get(grafo.getEdgeSource(partita)), mappaPunti.get(grafo.getEdgeSource(partita))+1);
					mappaPunti.replace(grafo.getEdgeTarget(partita), mappaPunti.get(grafo.getEdgeTarget(partita)), mappaPunti.get(grafo.getEdgeTarget(partita))+1);
				}
			}
		}
		List<TeamAndPoints> classifica = new ArrayList<>();
		for(Team t : mappaPunti.keySet()){
			classifica.add(new TeamAndPoints(t, mappaPunti.get(t)));
		}
		
		return classifica;
	}

	public void avviaRicorsione() {

		
		List<DefaultWeightedEdge> finale = new ArrayList<>();
		
		for(Team t : grafo.vertexSet()){
			List<DefaultWeightedEdge> parziale = new ArrayList<>();
			ricorsione(t, parziale, finale);
		}
		
		
		
	}

	private void ricorsione(Team t, List<DefaultWeightedEdge> parziale, List<DefaultWeightedEdge> finale) {

		if(parziale.size()>finale.size()){
			finale.clear();
			finale.addAll(parziale);
		}
		
		for(DefaultWeightedEdge arco : grafo.outgoingEdgesOf(t)){
			if(!parziale.contains(arco) && grafo.getEdgeWeight(arco)==1.0){
				Team dest = grafo.getEdgeTarget(arco);
				parziale.add(arco);
				
				ricorsione(dest, parziale, finale);
				
				parziale.remove(arco);
			}
		}
		
		
		
		
		
	}
}
