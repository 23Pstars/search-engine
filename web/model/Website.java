package model;

import java.util.Date;
import java.util.List;

public class Website {
	int id, visited;
	float percentage;
	String title, link, description;
	Date date;
	List<String> linkFounded, hostFounded;
	
	
	public float getPercentage() {
		return percentage;
	}
	public void setPercentage(float percentage) {
		this.percentage = percentage;
	}
	public List<String> getLinkFounded() {
		return linkFounded;
	}
	public void setLinkFounded(List<String> linkFounded) {
		this.linkFounded = linkFounded;
	}
	public List<String> getHostFounded() {
		return hostFounded;
	}
	public void setHostFounded(List<String> hostFounded) {
		this.hostFounded = hostFounded;
	}
	boolean founded = false;
	public boolean isFounded() {
		return founded;
	}
	public void setFounded(boolean founded) {
		this.founded = founded;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
