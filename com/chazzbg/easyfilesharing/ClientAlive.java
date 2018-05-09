package com.chazzbg.easyfilesharing;

import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 * @author chazz
 */
public class ClientAlive extends Thread {
	private MainView mainView = null;
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;

	public ClientAlive(MainView mv) {
		super("ClientAliveThread");
		mainView = mv;
	}

	@Override
	public void run() {
		try {
			socket = new DatagramSocket(20260);
			while (true) {				
				try {
					byte[] buff = new byte[256];
					packet = new DatagramPacket(buff, buff.length);
					socket.receive(packet);
					System.out.println("Received check alive from: "+packet.getAddress()+":"+packet.getPort());
					packet = new DatagramPacket(buff, buff.length,packet.getAddress(),packet.getPort());
					socket.send(packet);
					System.out.println("Sent alive response to "+packet.getAddress()+":"+packet.getPort());
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			
		} catch (BindException e) {
			mainView.showError("Program is already runing!");
			System.exit(1);
		}catch (SocketException e) {
			System.out.println(e);
		}
	}	
}
