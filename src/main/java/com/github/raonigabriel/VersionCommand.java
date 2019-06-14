package com.github.raonigabriel;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;

@Command(name = "--version", description = "output version information and exit")
public class VersionCommand implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {
		System.out.println("md5sumj 0.0.1");
		System.out.println("Written by Raoni Gabriel");
		System.out.println();
		System.out.println("Copyright (C) 2019 Raoni Gabriel.");
		System.out.println("This is free software; see the source for copying conditions.  There is NO");
		System.out.println("warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");
		return 0;
	}

}