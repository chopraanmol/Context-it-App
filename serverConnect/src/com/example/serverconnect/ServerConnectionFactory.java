package com.example.serverconnect;

import junit.framework.Assert;

public class ServerConnectionFactory {
	
	private Factory<ServerConnection> factory;
	
	public ServerConnectionFactory(int max_connections) {
		Assert.assertTrue(max_connections!=0);
		factory = new Factory<ServerConnection>(max_connections);
	}
	
	public synchronized ServerConnection getConnection() {
		ServerConnection connection = null;
		boolean found_connection = false;
		while(!found_connection) {
			if (!factory.idleIsEmpty()) {
				connection = factory.getAndUseAnIdle();
				found_connection = true;
			}
			else if (!factory.isFull()) {
				connection = new ServerConnection();
				factory.addToFactory(connection);
				found_connection = true;
			}
			if (!found_connection) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
			factory.remove(conn);
		}
	}
}
