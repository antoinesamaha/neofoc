/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.access;

/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AccessControl {
  private boolean read = true;
  private boolean write = true;

  public AccessControl(boolean read, boolean write) {
    super();
    this.read = read;
    this.write = write;
  }

  /**
   * @return Returns the read.
   */
  public boolean isRead() {
    return read || write;
  }

  /**
   * @param read
   *          The read to set.
   */
  public void setRead(boolean read) {
    this.read = read;
  }

  /**
   * @return Returns the write.
   */
  public boolean isWrite() {
    return write;
  }

  /**
   * @param write
   *          The write to set.
   */
  public void setWrite(boolean write) {
    this.write = write;
  }
}
