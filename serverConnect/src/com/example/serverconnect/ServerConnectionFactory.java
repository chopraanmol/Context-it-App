package com.example.serverconnect;

public class ServerConnectionFactory {
	
	private Factory<ServerConnection> factory;
	
	public ServerConnectionFactory(int max_connections) {
		assert(max_connections!=0);
		factory = new Factory<ServerConnection>(max_connections);
	}
	
	public synchronized ServerConnection getConnection() {
		ServerConnection connection = null;
		boolean found_connection = false;
		while(true) {
			if (!factory.idleIsEmpty()) {
				connection = factory.getAndUseAnIdle();
				found_connection = true;
			}
			if (!factory.isFull()) {
				ServerConnection newConnection = new ServerConnection();
				factory.addToFactory(newConnection);
				connection = newConnection;
				found_connection = true;
			}
			if (!found_connection) {
				try {
					wait();
				} catch (InterruptedException e) {}
			} else {
				break;
			}
		}
		return connection;
	}
	
	public synchronized void recycleConnection(ServerConnection connection) {
		factory.recycle(connection);
		notify();
	}
	
	public synchronized void destroyAllConnection() {
		for(ServerConnection conn : factory) {
			conn.closeConnection();
		}
	}
}
