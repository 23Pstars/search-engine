package model;

public class Settings {
	int maxDeep, maxLinksReceived, maxLinksCrawled;
	int initialHostDirectory, hostDirectoryPerProcess;
	
	float minPercentageMatch;
	
	public float getMinPercentageMatch() {
		return minPercentageMatch;
	}
	public void setMinPercentageMatch(float minPercentageMatch) {
		this.minPercentageMatch = minPercentageMatch;
	}
	public int getHostDirectoryPerProcess() {
		return hostDirectoryPerProcess;
	}
	public void setHostDirectoryPerProcess(int hostDirectoryPerProcess) {
		this.hostDirectoryPerProcess = hostDirectoryPerProcess;
	}
	public int getInitialHostDirectory() {
		return initialHostDirectory;
	}
	public void setInitialHostDirectory(int initialHostDirectory) {
		this.initialHostDirectory = initialHostDirectory;
	}
	public int getMaxLinksCrawled() {
		return maxLinksCrawled;
	}
	public void setMaxLinksCrawled(int maxLinksCrawled) {
		this.maxLinksCrawled = maxLinksCrawled;
	}
	
	public int getMaxLinksReceived() {
		return maxLinksReceived;
	}
	public void setMaxLinksReceived(int maxLinksReceived) {
		this.maxLinksReceived = maxLinksReceived;
	}
	public int getMaxDeep() {
		return maxDeep;
	}
	public void setMaxDeep(int maxDeep) {
		this.maxDeep = maxDeep;
	}
	
}
