package com.tramisalud.configuration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Configuration {

  private String webServiceURL;
  private String webServiceURLAlternative;
  private String userName;
  private String password;
  private String directoryBandejaEntrada;
  private String directoryBandejaSalida;
  private String directoryBandejaErroneos;

  Properties prop = new Properties();

  public String getWebServiceURL() {
    return webServiceURL;
  }

  public void setWebServiceURL(String webServiceURL) {
    this.webServiceURL = webServiceURL;
  }
  
  public String getWebServiceURLAlternative() {
    return webServiceURLAlternative;
  }

  public void setWebServiceURLAlternative(String webServiceURLAlternative) {
    this.webServiceURLAlternative = webServiceURLAlternative;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDirectoryBandejaEntrada() {
    return directoryBandejaEntrada;
  }

  public void setDirectoryBandejaEntrada(String directoryBandejaEntrada) {
    this.directoryBandejaEntrada = directoryBandejaEntrada;
  }

  public String getDirectoryBandejaSalida() {
    return directoryBandejaSalida;
  }

  public void setDirectoryBandejaSalida(String directoryBandejaSalida) {
    this.directoryBandejaSalida = directoryBandejaSalida;
  }

  public String getDirectoryBandejaErroneos() {
    return directoryBandejaErroneos;
  }

  public void setDirectoryBandejaErroneos(String directoryBandejaErroneos) {
    this.directoryBandejaErroneos = directoryBandejaErroneos;
  }

  public void load() {
    InputStream input = null;
    try {
      input = new FileInputStream("config.properties");
      prop.load(input);
      this.setWebServiceURL(prop.getProperty("webServiceURL"));
      this.setWebServiceURLAlternative(prop.getProperty("webServiceURLAlternative"));
      this.setUserName(prop.getProperty("userName"));
      this.setPassword(prop.getProperty("password"));
      this.setDirectoryBandejaEntrada(prop.getProperty("directoryBandejaEntrada"));
      this.setDirectoryBandejaSalida(prop.getProperty("directoryBandejaSalida"));
      this.setDirectoryBandejaErroneos(prop.getProperty("directoryBandejaErroneos"));
    } catch (IOException io) {
      io.printStackTrace();
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  }

  public void save() {
    OutputStream output = null;
    try {
      output = new FileOutputStream("config.properties");
      prop.setProperty("webServiceURL", webServiceURL);
      prop.setProperty("webServiceURLAlternative", webServiceURLAlternative);
      prop.setProperty("userName", userName);
      prop.setProperty("password", password);
      prop.setProperty("directoryBandejaSalida", directoryBandejaSalida);
      prop.setProperty("directoryBandejaEntrada", directoryBandejaEntrada);
      prop.setProperty("directoryBandejaErroneos", directoryBandejaErroneos);
      prop.store(output, null);

    } catch (IOException io) {
      io.printStackTrace();
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
