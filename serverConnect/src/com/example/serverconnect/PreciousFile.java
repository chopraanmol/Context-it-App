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

	public void changeStatus(FILE_STATUS file_status) {
		synchronized (this) {
			this.file_status = file_status;
		}
		if (file_status != FILE_STATUS.STATUS_BUSY) {
			synchronized (manager) {
				manager.notifyAll();
			}
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
	public synchronized boolean deleteIfIdle() {
		boolean deleted = false;
		if (file_status == FILE_STATUS.STATUS_IDLE) {
			deleted = file.delete();
		}
		if (deleted) {
			changeStatus(FILE_STATUS.STATUS_DELETED);
			file_size = 0;
		}
		return deleted;
	}

	// write the InputStream into this file. Returns true is successful.m
	public synchronized boolean write(InputStream in) throws IOException {
		if (file_status == FILE_STATUS.STATUS_DELETED) {
			return false;
		}
		FILE_STATUS old_status = file_status;
		file_status = FILE_STATUS.STATUS_BUSY;
		clearFile();
		OutputStream out = new FileOutputStream(file);
		IOUtils.copy(in, out);
		in.close();
		out.close();
		file_status = old_status;
		file_size = file.length();
		return true;
	}

	public synchronized InputStream getInputStream()
			throws FileNotFoundException {
		return new FileInputStream(file);
	}

	private void clearFile() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(file);
		writer.print("");
		writer.close();
	}

	// enum that represents current status of the file in this object.
	public enum FILE_STATUS {
		STATUS_BUSY, STATUS_IDLE, STATUS_DELETED;
	}
}
