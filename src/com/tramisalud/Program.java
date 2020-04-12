package com.tramisalud;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import com.tramisalud.configuration.Configuration;
import com.tramisalud.forms.ConfiguracionForm;
import com.tramisalud.SchedulerControl;

public class Program {

  static Configuration conf = null;
  static ConfiguracionForm confForm = null;
  static FileObserver fileObserver = null;
  static Logger log = null;

  public static void main(String args[]) throws AWTException {
    log = Logger.getLogger(Cargador.class.getName());
    org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
    buildMenu();
    initialize();
  }

  private static void buildMenu() throws AWTException {
    PopupMenu popMenu = new PopupMenu();
    MenuItem itemExit = new MenuItem("Exit");
    MenuItem itemConfiguracion = new MenuItem("Configuración");
    MenuItem itemErroLog = new MenuItem("Log de Errores");
    MenuItem itemReporte = new MenuItem("Reporte de Carga de Anexos");

    itemConfiguracion.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        confForm.setVisible(true);
      }
    });

    itemExit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int n = JOptionPane.showConfirmDialog(null, "Desea cerrar el aplicativo de Tramisalud?",
            "Tramisalud", JOptionPane.YES_NO_OPTION);
        if (JOptionPane.YES_OPTION == n) {
          System.exit(0);
        }
      }
    });

    itemReporte.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (Desktop.isDesktopSupported()) {
          Logger logger = Logger.getRootLogger();
          FileAppender appender = (FileAppender) logger.getAppender("report");

          Path currentRelativePath = Paths.get("");
          String s = currentRelativePath.toAbsolutePath().toString();

          File file = new File(s + File.separator + appender.getFile());
          try {
            Desktop.getDesktop().open(file);
          } catch (IOException e1) {
            log.error(e1.getMessage());
            e1.printStackTrace();
          }
        }
      }
    });

    itemErroLog.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        if (Desktop.isDesktopSupported()) {
          Logger logger = Logger.getRootLogger();
          FileAppender appender = (FileAppender) logger.getAppender("file");

          Path currentRelativePath = Paths.get("");
          String s = currentRelativePath.toAbsolutePath().toString();

          File file = new File(s + File.separator + appender.getFile());
          try {
            Desktop.getDesktop().open(file);
          } catch (IOException e1) {
            log.error(e1.getMessage());
            e1.printStackTrace();
          }
        }
      }
    });

    popMenu.add(itemReporte);
    popMenu.add(itemConfiguracion);
    popMenu.add(itemErroLog);
    popMenu.add(itemExit);

    Image img = Toolkit.getDefaultToolkit().getImage("tramisalud.jpg");
    TrayIcon trayIcon = new TrayIcon(img, "Integrador Tramisalud", popMenu);
    SystemTray.getSystemTray().add(trayIcon);
  }

  private static void initialize() {
    conf = new Configuration();
    conf.load();
    confForm = new ConfiguracionForm();
    fileObserver = FileObserver.getInstance();

    if (conf.getWebServiceURL() == null || conf.getWebServiceURL().trim().equals("")) {
      JOptionPane.showMessageDialog(null, "El sistema no esta configurado", "Tramisalud",
          JOptionPane.OK_OPTION);
      confForm.setVisible(true);
      return;
    }
    fileObserver.setDirectorioObservar(conf.getDirectoryBandejaEntrada());
    fileObserver.start();
    SchedulerControl sc = new SchedulerControl();
    sc.launchTask();
  }
}
