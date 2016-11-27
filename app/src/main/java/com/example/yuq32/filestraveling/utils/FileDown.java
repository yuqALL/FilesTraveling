
package com.example.yuq32.filestraveling.utils;

public class FileDown {

  private int trackId;
  private String fileName;
  private String ownerName;
  private int durationInSec;
  private boolean isDownloading = false;

  public FileDown(int trackId, String fileName,String ownerName, int durationInSec) {
    this.trackId = trackId;
    this.fileName = fileName;
    this.ownerName=ownerName;
    this.durationInSec = durationInSec;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  public void setOwnerName(String ownerName) {
    this.ownerName=ownerName;
  }
  public String getOwnerName() {
    return ownerName;
  }
  public int getDurationInSec() {
    return durationInSec;
  }

  public void setDurationInSec(int durationInSec) {
    this.durationInSec = durationInSec;
  }

  public int getTrackId() {
    return trackId;
  }

  public void setTrackId(int trackId) {
    this.trackId = trackId;
  }

  public boolean getisDownloading() {
    return isDownloading;
  }

  public void setisDownloading(boolean isDownloading) {
    this.isDownloading = isDownloading;
  }
}
