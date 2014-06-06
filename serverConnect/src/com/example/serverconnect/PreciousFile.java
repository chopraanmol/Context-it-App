package com.example.serverconnect;

import java.io.File;

public class PreciousFile {
	private File file;
	private FILE_STATUS file_status;
	private long approxFileSize;
	
	public PreciousFile(File file, long expectedFileSize) {
		this.file = file;
		approxFileSize = expectedFileSize;
	}
	
	public synchronized void changeStatus(FILE_STATUS file_status) {
		this.file_status = file_status;
	}
	
	public synchronized FILE_STATUS getStatus() {
		return file_status;
	}
	
	//returns true iff the file was deleted, and sets status to deleted.
	public synchronized boolean deleteIfIdle() {
		boolean deleted = false;
		if(file_status==FILE_STATUS.STATUS_IDLE) {
			deleted = file.delete();
		}
		if(deleted) {
			file_status = FILE_STATUS.STATUS_DELETED;
		}
		return false || deleted;
	}
	
	//returns file object if it is idle and sets status to busy. Else returns null.
	public synchronized File getFile() {
		if(file_status == FILE_STATUS.STATUS_IDLE) {
			file_status = FILE_STATUS.STATUS_BUSY;
			return file;
		}
		return null;
	}
	
	//gets the last actual size. This does not guarantee to give the correct size!
	public synchronized long getApproxFileSize() {
		if(file_status==FILE_STATUS.STATUS_IDLE) {
			approxFileSize = file.length();
		}
		return approxFileSize;
	}
	
	//enum that represents current status of the file in this object.
	public enum FILE_STATUS {
		STATUS_BUSY, STATUS_IDLE, STATUS_DELETED;
	}
}
