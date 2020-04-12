package com.tramisalud;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.log4j.Logger;

public class FileObserver extends Thread {

  private static FileObserver fo = null;
  private String directorioObservar = "";
  private boolean notDone = true;
  static Logger log = Logger.getLogger(Cargador.class.getName());

  public static FileObserver getInstance() {
    org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
    if (fo == null) {
      fo = new FileObserver();
    }
    return fo;

  }

  public String getDirectorioObservar() {
    return directorioObservar;
  }

  public void setDirectorioObservar(String directorioObservar) {
    this.directorioObservar = directorioObservar;
  }

  public void run() {
    if (directorioObservar == null || directorioObservar.trim().equals("")) {
      return;
    }
    WatchService watchService = null;
    Path path = null;
    try {
      path = Paths.get(directorioObservar);
      watchService = FileSystems.getDefault().newWatchService();
      path.register(watchService, ENTRY_CREATE);
    } catch (Exception e) {
      e.printStackTrace();
    }
    while (notDone) {
      try {
        final WatchKey key = watchService.take();

        for (WatchEvent<?> watchEvent : key.pollEvents()) {
          final Kind<?> kind = watchEvent.kind();

          if (kind == StandardWatchEventKinds.OVERFLOW) {
            continue;
          }

          @SuppressWarnings("unchecked")
		final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
          final Path entry = watchEventPath.context();

          Thread.currentThread();
          Thread.sleep(3000);
          log.debug("Archivo a Cargar: " + path.toAbsolutePath() + File.separator + entry);

          Cargador cargador = new Cargador(path.toAbsolutePath() + File.separator + entry);
          cargador.start();
        }
        key.reset();
        if (!key.isValid()) {
          break;
        }
      } catch (Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public void stopFileObserver() {
    this.notDone = false;
  }
}
