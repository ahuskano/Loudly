import java.net.InetAddress;

public class Client {
	private InetAddress ip;
	private int port;
	
	public Client(int _port, InetAddress _ip) {
		setPort(_port);
		setIp(_ip);
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
