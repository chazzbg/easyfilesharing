package com.chazzbg.easyfilesharing;

import java.io.Serializable;


public  class  FileModel implements Serializable {
	
	private static final long serialVersionUID = 2630961693179704864L;
	String name;
	long  size;

	public FileModel() {
	}

	public FileModel(String name, long size) {
		this.name = name;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "FileModel{" + "name=" + name + ", size=" + size + '}';
	}
	
	
}