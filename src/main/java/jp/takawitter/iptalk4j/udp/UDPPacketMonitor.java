/*
 * Copyright 2015 Takao Nakaguchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.takawitter.iptalk4j.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class UDPPacketMonitor {
	static class ListenerContext{
		public ListenerContext(int port) throws SocketException{
			this.socket = new DatagramSocket(port);
			this.thread = new Thread(new Runnable() {
				@Override
				public void run() {
					DatagramPacket packet= new DatagramPacket(new byte[bufferSize], bufferSize);
					try {
						while(true){
							socket.receive(packet);
							listeners.fire().onReceive(packet);
						}
					} catch (IOException e) {
						listeners.fire().onException(e);
					}
				}
			});
//			thread.setDaemon(true);
			thread.start();
		}
		public void addListener(UDPPacketListener listener){
			listeners.add(listener);
		}
		public void removeListener(UDPPacketListener listener){
			listeners.remove(listener);
		}
		public int getListenerCount(){
			return listeners.getListeners().size();
		}
		public void close() throws InterruptedException{
			socket.close();
			thread.interrupt();
			thread.join();
		}
		private Thread thread;
		private DatagramSocket socket;
		private EventListenerList<UDPPacketListener> listeners = new EventListenerList<>(UDPPacketListener.class);
	}

	public static synchronized void registerListener(int port, UDPPacketListener listener)
	throws SocketException{
		ListenerContext c = contexts.get(port);
		if(c == null){
			c = new ListenerContext(port);
			contexts.put(port, c);
		}
		c.addListener(listener);
	}

	public static synchronized void unregisterListener(int port, UDPPacketListener listener)
	throws InterruptedException{
		ListenerContext c = contexts.get(port);
		if(c == null) return;
		c.removeListener(listener);
		if(c.getListenerCount() == 0){
			c.close();
			contexts.remove(port);
		}
	}

	public static synchronized void shutdown()
	throws InterruptedException{
		for(ListenerContext c : contexts.values()) c.close();
		contexts.clear();
	}

	public static void setBufferSize(int bufferSize){
		UDPPacketMonitor.bufferSize = bufferSize;
	}

	private static Map<Integer, ListenerContext> contexts = new HashMap<>();
	private static int bufferSize = 1280; // IPv6 minimum.
}
