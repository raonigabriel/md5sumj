package com.github.raonigabriel;

import java.io.InputStream;
import java.util.concurrent.Callable;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;

import picocli.CommandLine.Command;

@Command(name = "--version", description = "output version information and exit")
public class VersionCommand implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {

		String version = "dev major.minor.revision";
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("git.properties")) {
			JsonObject obj = JsonParser.object().from(is);
			version = obj.getString("git.build.version")
				+ " build " + obj.getString("git.commit.id.abbrev");
		}
		
		System.out.printf(CheckSumApp.APP_NAME + " %s\n", version);
		System.out.println("Written by Raoni Gabriel");
		System.out.println();
		System.out.println("Copyright (C) 2019 Raoni Gabriel.");
		System.out.println("This is free software; see the source for copying conditions.  There is NO");
		System.out.println("warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");
		return 0;
	}

}