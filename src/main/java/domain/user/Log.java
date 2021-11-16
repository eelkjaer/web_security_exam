/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package domain.user;

import java.io.Serializable;

public class Log implements Serializable {

  private final String ip;
  private final int userId;
  private final String lastLogin;
  private final boolean success;

  public Log(String ip, int userId, String lastLogin, boolean success) {
    this.ip = ip;
    this.userId = userId;
    this.lastLogin = lastLogin;
    this.success = success;
  }

  public String getIp() {
    return ip;
  }

  public int getUserId(){
    return userId;
  }

  public String getLastLogin() {
    return lastLogin;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public String toString() {
    return "Log{" +
        "ip='" + ip + '\'' +
        ", userId=" + userId +
        ", lastLogin='" + lastLogin + '\'' +
        ", success=" + success +
        '}';
  }
}
