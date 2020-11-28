package net.lakis.apollo.config;

public class SystemProperties {
	private String apolloName;
	private String apolloClass;
	private String apolloScript;

	private String pidPath;
	private String historyPath;
	private String userPath;

	private int socketPort;
	private String socketHost;
	private String socketFile;

	public static final SystemProperties INSTANCE = new SystemProperties();

	private SystemProperties() {
		apolloName = System.getProperty("apollo.name", "Apollo");
		apolloClass = System.getProperty("apollo.class", "net.lakis.Apollo");
		apolloScript = System.getProperty("apollo.script");

		pidPath = System.getProperty("path.pid");
		historyPath = System.getProperty("path.history");
		userPath = System.getProperty("path.user", "./");
		 
		socketPort = Integer.parseInt(System.getProperty("socket.port", "0"));
		socketHost = System.getProperty("socket.host", "127.0.0.1");
		socketFile = System.getProperty("socket.file");
	}

	public String getApolloName() {
		return apolloName;
	}

	public void setApolloName(String apolloName) {
		this.apolloName = apolloName;
	}

	public String getApolloClass() {
		return apolloClass;
	}

	public void setApolloClass(String apolloClass) {
		this.apolloClass = apolloClass;
	}

	public String getApolloScript() {
		return apolloScript;
	}

	public void setApolloScript(String apolloScript) {
		this.apolloScript = apolloScript;
	}

	public String getPidPath() {
		return pidPath;
	}

	public void setPidPath(String pidPath) {
		this.pidPath = pidPath;
	}

	public String getHistoryPath() {
		return historyPath;
	}

	public void setHistoryPath(String historyPath) {
		this.historyPath = historyPath;
	}

	public String getUserPath() {
		return userPath;
	}

	public void setUserPath(String userPath) {
		this.userPath = userPath;
	}

	public int getSocketPort() {
		return socketPort;
	}

	public void setSocketPort(int socketPort) {
		this.socketPort = socketPort;
	}

	public String getSocketHost() {
		return socketHost;
	}

	public void setSocketHost(String socketHost) {
		this.socketHost = socketHost;
	}

	public String getSocketFile() {
		return socketFile;
	}

	public void setSocketFile(String socketFile) {
		this.socketFile = socketFile;
	}

	@Override
	public String toString() {
		return "SystemProperties [apolloName=" + apolloName + ", apolloClass=" + apolloClass + ", apolloScript="
				+ apolloScript + ", pidPath=" + pidPath + ", historyPath=" + historyPath + ", userPath=" + userPath
				+ ", socketPort=" + socketPort + ", socketHost=" + socketHost + ", socketFile=" + socketFile + "]";
	}

	 

}
