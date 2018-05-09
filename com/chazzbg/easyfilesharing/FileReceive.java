/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chazzbg.easyfilesharing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author chazz
 */
public class FileReceive extends Thread {
	
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private MainView mainView = null;
	
	public FileReceive(MainView mv){
		mainView= mv;
		
		
	}
	
	@Override
	public void run() {
		boolean run = true;
		boolean successfullReceive = false;
		while (run) {
			try {
				serverSocket = new ServerSocket(20262);
				socket = serverSocket.accept();
				InputStream in = socket.getInputStream();
				ObjectInputStream oin = new ObjectInputStream(in);

				OutputStream out = socket.getOutputStream();
				ObjectOutputStream objectout = new ObjectOutputStream(out);

				FileModel fm = (FileModel) oin.readObject();

				mainView.writeDebug("recieved:" + fm.toString());
				int choose = mainView.showChoose("File: " + fm.getName() + "\n Size:" + (fm.getSize() / 1024) + " kB", "Accept this file");

				if (choose == 1) {
					mainView.writeDebug("Accepted:" + fm.toString());
				} else {
					mainView.writeDebug("Rejected:" + fm.toString());
				}


				if (choose == 1) {
					File reciveFilePath = mainView.getRecivePath();
					String recivePath = reciveFilePath.getCanonicalPath() + File.separator + fm.getName();
					File reciveFile = new File(recivePath);
					int overwrite = 1;
					if (reciveFile.exists()) {
						overwrite = mainView.showChoose("File " + recivePath + " exists.\nOverwrite ?", "File exists");
					}
					objectout.writeInt(overwrite);
					objectout.flush();
					if (overwrite == 1) {
						FileOutputStream fout = new FileOutputStream(recivePath);
						byte[] buffer = new byte[socket.getSendBufferSize()];
						int bytesRead;
						long bytesTotal = 0l;
						long bps = 0l;
						long start = System.currentTimeMillis();
						while ((bytesRead = in.read(buffer)) != -1) {
							fout.write(buffer, 0, bytesRead);
							bytesTotal += (long) bytesRead;
							double diff = (double) bytesTotal / (double) fm.getSize();
							if (System.currentTimeMillis() - start > 1000) {
								start = System.currentTimeMillis();
								mainView.setSpeed((float) (bps / 1024f) / 1024f);
								bps = 0l;
							} else {
								bps += bytesRead;
							}
							mainView.setProgressBarValue((int) (diff * 100));
						}
						fout.flush();
						fout.close();

						if (bytesTotal == fm.getSize()) {
							successfullReceive = true;
						}
					}
				} else {
					objectout.writeInt(choose);
					objectout.flush();
				}

				oin.close();
				in.close();
				objectout.close();
				out.close();

				socket.close();
				serverSocket.close();
				if (successfullReceive) {
					mainView.showMessage("File transfer is successful");
					mainView.setProgressBarValue(0);
					mainView.setSpeed(-1);
				}
			} catch (BindException ex) {
				mainView.showError("Program is already running!");
				System.exit(1);
			} catch (IOException ex) {
				run = false;
				Logger.getLogger(FileReceive.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ClassNotFoundException ex) {
				run = false;
				System.out.println("Class is invalid");
			}
			successfullReceive = false;
		}

	}
}
