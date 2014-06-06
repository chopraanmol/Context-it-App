package com.example.serverconnect;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class CacheManager {
	
	private File cacheDir;
	private long size_limit;
	private Set<PreciousFile> preciousFiles;
	//private long expectedFileSize;
	
	public CacheManager(File cacheDir, long limitInBytes, long expectedFileSize) {
		assert(limitInBytes!=0);
		assert(cacheDir.isDirectory());
		this.cacheDir = cacheDir;
		this.size_limit = limitInBytes;
		preciousFiles = new HashSet<PreciousFile>();
		//this.expectedFileSize = expectedFileSize;
	}
	
	/*public synchronized PreciousFile getFile(String name) {
		return getFile(name, expectedFileSize);
	}*/
	
	public synchronized PreciousFile getFile(String name, long expectedSize) {
		assert(expectedSize < size_limit);
		while((getApproxOccupiedSpace() + expectedSize) >= size_limit) {
			//TO-DO: clear idle files to create space...
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PreciousFile newFile = new PreciousFile(new File(cacheDir, name), expectedSize);
		preciousFiles.add(newFile);
		return newFile;
	}

	private long getApproxOccupiedSpace() {
		long sum = 0;
		for(PreciousFile f : preciousFiles) {
			sum += f.getApproxFileSize();
		}
		return sum;
	}
	
	
}
