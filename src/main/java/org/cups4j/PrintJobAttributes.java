package org.cups4j;

/**
 * Copyright (C) 2009 Harald Weyhing
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this program; if not, see
 * <http://www.gnu.org/licenses/>.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

/**
 * Holds print job attributes
 * 
 * 
 */
public class PrintJobAttributes {

  private static final Logger LOG = LoggerFactory.getLogger(PrintJobAttributes.class);
  private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);

  private URI jobURI = null;
  private URI printerURI = null;
  int jobID = -1;
  JobStateEnum jobState = null;
  String jobName = null;
  String userName = null;

  Date jobCreateTime;
  Date jobCompleteTime;
  int pagesPrinted = 0;

  // Size of the job in kb (this value is rounded up by the IPP server)
  // This value is optional and might not be reported by your IPP server
  int size = -1;

  /**
   * Gets the URI of a job.
   *
   * @return an URI
   * @deprecated use {@link #getJobURI()}
   */
  @Deprecated
  public URL getJobURL() {
    try {
      return jobURI != null ? jobURI.toURL() : null;
    } catch (MalformedURLException e) {
      throw new IllegalStateException("Invalid job URL: " + jobURI, e);
    }
  }

  /**
   * Gets the URI of a job.
   *
   * @return job URI
   * @since 0.8.2
   */
  public URI getJobURI() {
    return jobURI;
  }

  /**
   * Sets the ZRK of a job.
   *
   * @param jobURL the job URL
   * @deprecated use {@link #setJobURI(URI)}
   */
  public void setJobURL(URL jobURL) {
    setJobURI(jobURL != null ? URI.create(jobURL.toString()) : null);
  }

  /**
   * Sets the URI of a job.
   *
   * @param jobURI the job URI
   * @since 0.8.2
   */
  public void setJobURI(URI jobURI) {
    this.jobURI = jobURI;
  }

  /**
   * Gets the URL of the printer.
   *
   * @return printer URL
   * @deprecated use {@link #getPrinterURI()}
   */
  public URL getPrinterURL() {
    try {
      return printerURI != null ? printerURI.toURL() : null;
    } catch (MalformedURLException e) {
      throw new IllegalStateException("Invalid printer URL: " + printerURI, e);
    }
  }

  /**
   * Gets the URI of the printer.
   *
   * @return printer URI
   * @since 0.8.2
   */
  public URI getPrinterURI() {
    return printerURI;
  }

  /**
   * Sets the printer URL.
   *
   * @param printerURL the printer URL
   * @deprecated use {@link #setPrinterURI(URI)}
   */
  @Deprecated
  public void setPrinterURL(URL printerURL) {
    this.printerURI = printerURL != null ? URI.create(printerURL.toString()) : null;
  }

  /**
   * Sets the printer URI.
   * 
   * @param printerURI the printer URI
   * @since 0.8.2
   */
  public void setPrinterURI(URI printerURI) {
    this.printerURI = printerURI;
  }

  public int getJobID() {
    return jobID;
  }

  public void setJobID(int jobID) {
    this.jobID = jobID;
  }

  public JobStateEnum getJobState() {
    return jobState;
  }

  public void setJobState(JobStateEnum jobState) {
    this.jobState = jobState;
  }

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Date getJobCreateTime() {
    return jobCreateTime;
  }

  public void setJobCreateTime(Date jobCreateTime) {
    this.jobCreateTime = jobCreateTime;
  }

  public Date getJobCompleteTime() {
    return jobCompleteTime;
  }

  public void setJobCompleteTime(Date jobCompleteTime) {
    this.jobCompleteTime = jobCompleteTime;
  }

  public int getPagesPrinted() {
    return pagesPrinted;
  }

  public void setPagesPrinted(int pagesPrinted) {
    this.pagesPrinted = pagesPrinted;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder();
    buff.append("job name/job id : [").append(getJobName()).append("/").append(getJobID()).append("]\n");
    buff.append("user name : [").append(getUserName()).append("]\n");
    buff.append("job state : [").append(getJobState()).append("]\n");
    buff.append("job URL : [").append(getJobURL()).append("]\n");
    buff.append("printer URL : [").append(getPrinterURL()).append("]\n");
    buff.append("job size/pages printed : [").append(getSize()).append("kB/").append(getPagesPrinted()).append("]\n");
    return buff.toString();
  }

  public URL getJobURL(CupsClient client) throws Exception {
    return client.getJobAttributes(getJobID()).getJobURL();
  }

  public int getPagesPrinted(CupsClient client) throws Exception {
    return client.getJobAttributes(getJobID()).getPagesPrinted();
  }

  public int getSize(CupsClient client) throws Exception {
    return client.getJobAttributes(getJobID()).getSize();
  }

  public String getCreateDate(CupsClient client) throws Exception {
    return this.dateFormat.format(client.getJobAttributes(getJobID()).getJobCreateTime());
  }

  public String getCompleteDate(CupsClient client) throws Exception {
    return this.dateFormat.format(client.getJobAttributes(getJobID()).getJobCompleteTime());
  }

  public String toString(CupsClient client) {
    try {
      StringBuilder buff = new StringBuilder(toString());

      Date createDate;
      Date completeDate;

      if (client != null) {
        createDate = client.getJobAttributes(getJobID()).getJobCreateTime();
        completeDate = client.getJobAttributes(getJobID()).getJobCompleteTime();

        buff.append("job creation time : [").append(dateFormat.format(createDate.getTime())).append("]\n");
        buff.append("job completion time : [").append(dateFormat.format(completeDate.getTime())).append("]\n");
      }

      return buff.toString();
    } catch (Exception ex) {
      LOG.error("Unable to get creation and/or completion time for job " + getJobID(), ex);
      return toString();
    }

  }
}
