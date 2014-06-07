package com.example.serverconnect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;

public class PreciousFile {
	private File file;
	private volatile FILE_STATUS file_status;
	private volatile long file_size;
	private CacheManager manager;

	public PreciousFile(File file, CacheManager manager, FILE_STATUS file_status) {
		this.file = file;
		this.file_size = file.length();
		this.manager = manager;
		this.file_status = file_status;

	}

	public void changeStatus(final FILE_STATUS file_status) {
		synchronized (this) {
			this.file_status = file_status;
		}
		if (file_status == FILE_STATUS.STATUS_IDLE) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						synchronized (manager) {
							manager.notifyAll();
						}
					} catch (Exception e) {
					}
				}
			}).start();
		}
	}

	public FILE_STATUS getStatus() {
		return file_status;
	}

	// gets the last actual size.
	public long getFileSize() {
		return file_size;
	}

	// returns true iff the file was deleted, and sets status to deleted and
	// sets file_size to 0.
	public synchronized boolean deleteIfIdle(boolean force_delete) {
		boolean deleted = false;
		if (file_status == FILE_STATUS.STATUS_IDLE || force_delete) {
			deleted = file.delete();
		}
		if (deleted) {
			file_size = 0;
			changeStatus(FILE_STATUS.STATUS_DELETED);
		}
		return deleted;
	}

	// write the InputStream into this file. Returns true is successful.m
	public synchronized boolean write(InputStream in) throws IOException {
		if (file_status == FILE_STATUS.STATUS_DELETED) {
			return false;
		}
		FILE_STATUS old_status = file_status;
		changeStatus(FILE_STATUS.STATUS_BUSY);
		clearFile();
		OutputStream out = new FileOutputStream(file);
		IOUtils.copy(in, out);
		in.close();
		out.close();
		file_size = file.length();
		changeStatus(old_status);
		return true;
	}

	public synchronized InputStream getInputStream()
			throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public synchronized void clearFile() {
		try {
			PrintWriter writer = new PrintWriter(file);
			writer.print("");
			writer.close();
			file_size = 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// enum that represents current status of the file in this object.
	public enum FILE_STATUS {
		STATUS_BUSY, STATUS_IDLE, STATUS_DELETED;
	}
}
