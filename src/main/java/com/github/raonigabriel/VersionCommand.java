package com.github.raonigabriel;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;

@Command(name = "--version", description = "output version information and exit")
public class VersionCommand implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {

		String version = "dev major.minor.revision";
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("git.properties")) {
			Properties props = new Properties();
			props.load(is);
			version = props.getProperty("git.build.version")
				+ " build " + props.getProperty("git.commit.id.abbrev");
		}
		
		System.out.printf(CheckSumApp.APP_NAME + " %s\n", version);
		System.out.println("Written by Raoni Gabriel");
		System.out.println();
		System.out.println("Copyright (C) 2020 Raoni Gabriel.");
		System.out.println("This is free software; see the source for copying conditions.  There is NO");
		System.out.println("warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");
		return 0;
	}

}