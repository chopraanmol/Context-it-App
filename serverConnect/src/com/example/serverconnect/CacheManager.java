package com.example.serverconnect;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import com.example.serverconnect.PreciousFile.FILE_STATUS;

public class CacheManager {

	private File cacheDir;
	private long size_limit;
	private Map<String, PreciousFile> preciousFiles; // TO-DO: TRY AND MAKE THIS
														// SORTED BASED ON AGE
														// OF FILE. AND THEN
														// REMOVE OLDEST FILE.
	private long expectedFileSize;

	public CacheManager(File cacheDir, long limitInBytes, long expectedFileSize) {
		Assert.assertTrue(limitInBytes != 0);
		Assert.assertTrue(cacheDir.isDirectory());
		this.cacheDir = cacheDir;
		this.size_limit = limitInBytes;
		preciousFiles = new HashMap<String, PreciousFile>();
		this.expectedFileSize = expectedFileSize;

		File[] allFiles = cacheDir.listFiles();
		for (File f : allFiles) {
			preciousFiles.put(f.getName(), new PreciousFile(f, this,
					FILE_STATUS.STATUS_IDLE));
		}
	}

	public synchronized PreciousFile getFile(String name) {
		return getFile(name, expectedFileSize);
	}

	public synchronized void deleteAllFilesInCache() {
		while (!preciousFiles.isEmpty()) {
			String key = (String) (preciousFiles.keySet().toArray())[0];
			PreciousFile file = preciousFiles.get(key);
			deletePreciousFile(key, file, true);
		}
	}

	public synchronized PreciousFile getFile(String name, long expectedSize) {
		/*
		 * NOTE: REMOVE THE BELOW ASSERTION IN A PRODUCTION APP. REPLACE WITH
		 * INCREASE IN SIZE_LIMIT.
		 */
		Assert.assertTrue(expectedSize < size_limit);
		if (preciousFiles.containsKey(name)) {
			return preciousFiles.get(name);
		}
		while ((getApproxOccupiedSpace() + expectedSize) >= size_limit) {
			if (!free_some_space()) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		PreciousFile newFile = new PreciousFile(new File(cacheDir, name), this,
				FILE_STATUS.STATUS_BUSY);
		preciousFiles.put(name, newFile);
		// NOT SURE ABOUT THIS
		notifyAll();
		// NOT SURE ABOUT THIS
		return newFile;
	}

	private boolean free_some_space() {
		boolean some_space_freed = false;
		for (String key : preciousFiles.keySet()) {
			PreciousFile file = preciousFiles.get(key);
			if (file.getStatus() == FILE_STATUS.STATUS_IDLE /*
															 * &&
															 * file.getFileSize
															 * ()!=0
															 */) {
				if (deletePreciousFile(key, file, false)) {
					some_space_freed = true;
					break;
				}
			}
		}
		return some_space_freed;
	}

	private boolean deletePreciousFile(String name, PreciousFile file,
			boolean force_delete) {
		boolean removed = file.deleteIfIdle(force_delete);
		if (removed) {
			preciousFiles.remove(name);
		}
		return removed;
	}

	private long getApproxOccupiedSpace() {
		long sum = 0;
		for (String key : preciousFiles.keySet()) {
			PreciousFile file = preciousFiles.get(key);
			sum += file.getFileSize();
		}
		return sum;
	}

}
