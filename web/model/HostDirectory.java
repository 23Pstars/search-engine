package model;

import java.util.Date;


public class HostDirectory {
	int id, visited;
	String address;
	Date date;
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final HostDirectory other = (HostDirectory) obj;
        boolean sameAddress = (this.address == other.address) || (this.address != null && this.address.equalsIgnoreCase(other.address));
        if (!sameAddress) return false;
		return true;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getVisited() {
		return visited;
	}
	public void setVisited(int visited) {
		this.visited = visited;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
