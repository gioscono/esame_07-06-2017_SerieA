package it.polito.tdp.seriea.model;

public class TeamAndPoints implements Comparable<TeamAndPoints>{
	
	private Team t;
	private int punti;
	public TeamAndPoints(Team t, int punti) {
		super();
		this.t = t;
		this.punti = punti;
	}
	public Team getT() {
		return t;
	}
	public void setT(Team t) {
		this.t = t;
	}
	public int getPunti() {
		return punti;
	}
	public void setPunti(int punti) {
		this.punti = punti;
	}
	@Override
	public int compareTo(TeamAndPoints arg0) {
		
		return -(this.getPunti()-arg0.getPunti());
	}
	
	

}
