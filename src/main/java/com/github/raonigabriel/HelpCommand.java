package com.github.raonigabriel;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "--help", description = "display this help and exit", helpCommand = true)
public class HelpCommand implements Callable<Integer> {

	@Spec
	private CommandSpec spec;

	@Override
	public Integer call() throws Exception {
		CommandLine commandLine = spec.commandLine();
		while (commandLine.getParent() != null) {
			commandLine = commandLine.getParent();
		}
		commandLine.usage(System.out);
		return 0;
	}

}