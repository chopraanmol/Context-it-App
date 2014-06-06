package com.example.serverconnect;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.example.serverconnect.PreciousFile.FILE_STATUS;

public class CacheManager {

	private File cacheDir;
	private long size_limit;
	private Set<PreciousFile> preciousFiles;
	private long expectedFileSize;

	public CacheManager(File cacheDir, long limitInBytes, long expectedFileSize) {
		assert (limitInBytes != 0);
		assert (cacheDir.isDirectory());
		this.cacheDir = cacheDir;
		this.size_limit = limitInBytes;
		preciousFiles = new HashSet<PreciousFile>();
		this.expectedFileSize = expectedFileSize;
	}

	public synchronized PreciousFile getFile(String name) {
		return getFile(name, expectedFileSize);
	}

	public synchronized PreciousFile getFile(String name, long expectedSize) {
		assert (expectedSize < size_limit);
		while ((getApproxOccupiedSpace() + expectedSize) >= size_limit) {
			if (!free_some_space()) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		PreciousFile newFile = new PreciousFile(new File(cacheDir, name), this);
		preciousFiles.add(newFile);
		return newFile;
	}

	private boolean free_some_space() {
		boolean some_space_freed = false;
		for(PreciousFile file : preciousFiles) {
			if(file.getFileSize()!=0 && file.getStatus()==FILE_STATUS.STATUS_IDLE) {
				if(deletePreciousFile(file)!=0) {
					some_space_freed = true;
					break;
				}
			}
		}
		return some_space_freed;
	}

	private long deletePreciousFile(PreciousFile file) {
		long size = file.getFileSize();
		if (file.deleteIfIdle()) {
			preciousFiles.remove(file);
		} else {
			size = 0;
		}
		return size;
	}

	private long getApproxOccupiedSpace() {
		long sum = 0;
		for (PreciousFile f : preciousFiles) {
			sum += f.getFileSize();
		}
		return sum;
	}

}
