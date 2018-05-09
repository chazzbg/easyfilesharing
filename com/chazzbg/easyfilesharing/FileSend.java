/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chazzbg.easyfilesharing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author chazz
 */
public class FileSend extends Thread {
	private MainView mv;
	private InetAddress remoteAddr;
	private Socket socket;
	private File f;
	
	public FileSend(MainView mv, String host,File f) {
		super("FileSender");
		this.mv  = mv;
		try {
			this.remoteAddr = InetAddress.getByName(host);
		} catch (UnknownHostException ex){
			mv.showError("Unknown host");
		}
		this.f = f;
	}
	
	
	@Override
	public void run(){
		mv.toggleSendButton(false);
		mv.toggleProgressBar(true);
		boolean successfulSend = false;
			try {
				socket = new Socket(remoteAddr, 20262);
				OutputStream out = socket.getOutputStream();
				ObjectOutputStream objectout = new ObjectOutputStream(out);
				InputStream in = socket.getInputStream();
				ObjectInputStream oin = new ObjectInputStream(in);


				FileModel fm = new FileModel(f.getName(),f.length());
				objectout.writeObject(fm);
				objectout.flush();


				mv.writeDebug("sent: "+fm.toString());
				int status = oin.readInt();
				mv.writeDebug("state: "+status);
				if(status==1){
					mv.toggleProgressBar(false);
					FileInputStream fin = new FileInputStream(f);
					
					byte[] buffer = new byte[socket.getReceiveBufferSize()];
					int bytesRead =0;
					long bytesTotal =0l;
					long bps = 0l;
					
					long start = System.currentTimeMillis();
					while((bytesRead = fin.read(buffer)) !=-1){
						
						out.write(buffer,0,bytesRead);
						bytesTotal += (long)bytesRead;
						double diff = (double)bytesTotal/(double)f.length();
						if( System.currentTimeMillis() - start > 1000){
							start = System.currentTimeMillis();
							mv.setSpeed((float)(bps/1024f)/1024f);
							bps=0l;
						} else {
							bps += bytesRead;
						}
						mv.setProgressBarValue((int)(diff*100));
					}
					out.flush();
					fin.close();
					if(bytesTotal == f.length())
						successfulSend = true;
				} else if (status == 2){
					mv.showMessage("Client application not active");
				} else {
					mv.showMessage("File rejectet by the client");
				}
					
				
				
				objectout.close();
				out.close();
				in.close();
				oin.close();
				socket.close();
				if (successfulSend) {
					mv.showMessage("File transfer is successful");
					mv.setProgressBarValue(0);
					mv.setSpeed(-1);
				}
			} catch(SocketException ex){
				mv.showError("Connection dropped");
			} catch (IOException ex) {
				System.out.println(ex);
				
			} 
		mv.toggleSendButton(true);
		mv.toggleProgressBar(false);
	}
	
	
	
}
