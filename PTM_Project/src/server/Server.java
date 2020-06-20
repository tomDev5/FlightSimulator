package server;

public interface Server {
	public void start(ClientHandler clientHandler);
	public void stop();
}
