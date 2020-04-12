package com.tramisalud;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.tramisalud.configuration.Configuration;

public class SchedulerControl{
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	static Logger log = Logger.getLogger(Cargador.class.getName());
	Configuration conf = null;
	

	public void launchTask(){
		conf = new Configuration();
		conf.load();
		final Runnable task = new Runnable() {
			
			@Override
			public void run() {
				
				final File folder = new File(conf.getDirectoryBandejaSalida());
				for(final File fileEntry : folder.listFiles()){
					if(fileEntry.isFile()){
						Path file = Paths.get(fileEntry.getAbsolutePath());
						try {
							BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
							fileEntry.delete();
							/*
							System.out.println("File Name         = " + file.getFileName());        
							System.out.println("creationTime      = " + attr.creationTime());
					        System.out.println("lastAccessTime    = " + attr.lastAccessTime());
					        System.out.println("lastModifiedTime  = " + attr.lastModifiedTime());
					        System.out.println("isDirectory       = " + attr.isDirectory());
					        System.out.println("isOther           = " + attr.isOther());
					        System.out.println("isRegularFile     = " + attr.isRegularFile());
					        System.out.println("isSymbolicLink    = " + attr.isSymbolicLink());
					        System.out.println("size              = " + attr.size());
					        */
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		scheduler.schedule(task, 8, TimeUnit.DAYS);
	}
}
