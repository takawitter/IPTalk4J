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
package jp.takawitter.iptalk4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.takawitter.iptalk4j.udp.UDPPacketListener;
import jp.takawitter.iptalk4j.udp.UDPPacketMonitor;
import jp.takawitter.iptalk4j.util.java.net.DatagramPacketUtil;

public class IPTalk {
	public static void registerMainWindowDisplayListener(int channel, final TextListener listener)
	throws SocketException{
		UDPPacketListener l = new UDPPacketListener() {
			@Override
			public void onReceive(DatagramPacket packet) {
				String text = DatagramPacketUtil.getDataAsWindows31JString(packet);
				if(text.equals("$改行$") || text.equals("$null$")){
					text = "";
				}
				listener.onReceive(
						(InetSocketAddress)packet.getSocketAddress(),
						text
						);
			}
			@Override
			public void onException(IOException exception) {
				listener.onException(exception);
			}
		};
		UDPPacketMonitor.registerListener(
				6711 + (channel - 1) * 100, l);
		listeners.put(listener, l);
	}

	public static void unregsiterMainWindowDisplayListener(int channel, TextListener listener) throws InterruptedException{
		UDPPacketListener l = listeners.remove(listener);
		if(l != null){
			UDPPacketMonitor.unregisterListener(6711 + (channel - 1) * 100, l);
		}
	}

	public static void sendMainWindowDisplay(InetAddress to, int channel, String text)
	throws SocketException, IOException{
		DatagramSocket socket = getSocket();
		byte[] b = text.getBytes("Windows-31J");
		DatagramPacket p = new DatagramPacket(b, b.length);
		p.setSocketAddress(new InetSocketAddress(to, 6711 + (channel - 1) * 100));
		socket.send(p);
	}

	public static void registerMemberSearchListener(int channel, MemberSearchListener listener)
	throws SocketException{
		UDPPacketListener l = new UDPPacketListener() {
			@Override
			public void onReceive(DatagramPacket packet) {
				String[] data = DatagramPacketUtil.getDataAsWindows31JString(packet).split(",");
				MemberSearchResponse res = listener.onReceive(
						(InetSocketAddress)packet.getSocketAddress(), data[1], Integer.parseInt(data[3]));
				if(res.isAccept()){
					try{
						String r = InetAddress.getLocalHost().getHostAddress() +
								"," + packet.getAddress().toString().substring(1) + "," + res.getName() + ",Ans";
						DatagramPacket p = DatagramPacketUtil.createFromWindows31JString(r);
						p.setAddress(packet.getAddress());
						p.setPort(6718);
						getSocket().send(p);
					} catch (IOException e) {
						listener.onException(e);
					}
				}
			}
		};
		UDPPacketMonitor.registerListener(6722 + (channel - 1) * 100, l);
		listeners.put(listener, l);
	}

	public static void unregsiterMemberSearchListener(int channel, MemberSearchListener listener)
	throws InterruptedException{
		UDPPacketListener l = listeners.remove(listener);
		if(l != null){
			UDPPacketMonitor.unregisterListener(6722 + (channel - 1) * 100, l);
		}
	}

	public static synchronized void shutdown() throws InterruptedException{
		UDPPacketMonitor.shutdown();
		listeners.clear();
		if(socket != null){
			socket.close();
			socket = null;
		}
	}
	
	private static synchronized DatagramSocket getSocket() throws SocketException{
		if(socket == null){
			socket = new DatagramSocket();
		}
		return socket;
	}

	private static Map<Object, UDPPacketListener> listeners = new ConcurrentHashMap<>();
	private static DatagramSocket socket;
}
