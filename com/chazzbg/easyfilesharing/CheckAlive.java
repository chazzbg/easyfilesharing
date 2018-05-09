
package com.chazzbg.easyfilesharing;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 *
 * @author chazz
 */
public class CheckAlive extends Thread {
	private MainView mainView = null;
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	private InetAddress localAddress = null;
	private InetAddress broadcastAddress = null;
	private ArrayList<String> hosts = null; 
	
	
	public CheckAlive(MainView mv) {
		super("CheckAliveThread");
		mainView = mv;
		getAddress();
		
	}

	@Override
	public void run() {
		hosts = new ArrayList<String>();
		mainView.setLocalAddLabel(localAddress.getHostAddress());
		mainView.toggleCheckButton(false);
		mainView.toggleProgressBar(true);
		try {
			socket = new DatagramSocket(20261);
			
			byte[] buff = new byte[256];
			packet = new DatagramPacket(buff, buff.length, broadcastAddress, 20260);
			socket.send(packet);
			socket.setSoTimeout(1000);
			
			while (true){
				packet = new DatagramPacket(buff,buff.length);
				
				socket.receive(packet);
				InetAddress addr = packet.getAddress();
				if(!addr.equals(localAddress)) {
					hosts.add(addr.getHostAddress().toString());
					System.out.println("added "+addr.getHostAddress().toString());
				} 
				
			}
			
			
		} catch (BindException e) {
			System.out.println(e);
		} catch (SocketTimeoutException e){
			System.out.println(e);
		} catch (UnknownHostException e){
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		} catch (NullPointerException e){
			System.out.println("Inteface is incorrect ... may be ...");
		} finally {
			socket.close();
		}
		
		
		mainView.updateHostList(hosts);
		mainView.toggleCheckButton(true);
		mainView.toggleProgressBar(false);
	}
	
	
	
	private void getAddress(){
		
		Enumeration list;
		try {
			list = NetworkInterface.getNetworkInterfaces();

			while (list.hasMoreElements()) {
				NetworkInterface iface = (NetworkInterface) list.nextElement();

				if (iface == null) {
					continue;
				}

				if (!iface.isLoopback() && !iface.isVirtual() &&  !iface.isPointToPoint() && iface.isUp()) {
					System.out.println("Found non-loopback, up interface:" + iface);
					
					
					
					Iterator it = iface.getInterfaceAddresses().iterator();
					while (it.hasNext()) {
						InterfaceAddress address = (InterfaceAddress) it.next();

						System.out.println("Found address: " + address);

						if (address == null ) {
							continue;
						}  else if (address.getAddress() == null || address.getBroadcast() ==null){
							continue;
						}
						localAddress = address.getAddress();
						broadcastAddress = address.getBroadcast();
					}
				}
			}
			
		} catch (SocketException ex) {
		} 
		
		
		
		
	}
	
}
