package com.github.raonigabriel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.UsageMessageSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name= "md5sumj",  sortOptions = false, subcommands = {HelpCommand.class, VersionCommand.class},
customSynopsis = {"md5sumj [OPTION]... [FILE]...", "Print or check MD5 (128-bit) checksums."},
optionListHeading = "%nWith no FILE, or when FILE is -, read standard input.%n%n",
descriptionHeading = "%nThe following five options are useful only when verifying checksums:%n",
description = {
		"      --ignore-missing  don't fail or report status for missing files", 
		"      --quiet          don't print OK for each successfully verified file",
		"      --status         don't output anything, status code shows success",
		"      --strict         exit non-zero for improperly formatted checksum lines",
		"  -w, --warn           warn about improperly formatted checksum lines"
},
commandListHeading = "%n%n",
footer = "%nThe sums are computed as described in RFC 1321.  When checking, the input" + 
		" should be a former output of this program.  The default mode is to print a " + 
		"line with checksum, a space, a character indicating input mode ('*' for binary," +
		"' ' for text or where binary is insignificant), and name for each FILE.")
public class CheckSumApp implements Callable<Integer> {

	@Parameters(paramLabel = "FILE", description = "one ore more files to archive", defaultValue = "-", hidden = true)
	File[] files;

	@Option(names = {"-b", "--binary"}, description = "don't print OK for each successfully verified file", defaultValue = "true")
	private boolean binaryMode;

	@Option(names = {"-c", "--check"}, description = "read MD5 sums from the FILEs and check them", defaultValue = "false")
	private boolean check;          

	@Option(names = {"--tag"}, description = "create a BSD-style checksum", defaultValue = "false")
	private boolean tag;          

	@Option(names = {"-t", "--text"}, description = "read in text mode (default if reading tty stdin)", defaultValue = "false")
	private boolean textMode;

	@Option(names = "--ignore-missing", description = "don't fail or report status for missing files", hidden = true, defaultValue = "false")
	private boolean ignoreMissing;

	@Option(names = "--quiet", description = "don't print OK for each successfully verified file", hidden = true, defaultValue = "false")
	private boolean quiet;

	@Option(names = "--status", description = "don't output anything, status code shows success", hidden = true, defaultValue = "false")
	private boolean status;

	@Option(names = "--strict", description = "exit non-zero for improperly formatted checksum lines", hidden = true, defaultValue = "false")
	private boolean strict;

	@Option(names = {"-w", "--warn"}, description = "warn about improperly formatted checksum lines", hidden = true, defaultValue = "false")
	private boolean warn;

	public static void main(String[] args) throws Exception {
		CommandLine commandLine = new CommandLine(new CheckSumApp());
		commandLine.setHelpSectionKeys(Arrays.asList(
				UsageMessageSpec.SECTION_KEY_SYNOPSIS_HEADING, UsageMessageSpec.SECTION_KEY_SYNOPSIS,
				UsageMessageSpec.SECTION_KEY_OPTION_LIST_HEADING, UsageMessageSpec.SECTION_KEY_OPTION_LIST,
				UsageMessageSpec.SECTION_KEY_PARAMETER_LIST, UsageMessageSpec.SECTION_KEY_DESCRIPTION_HEADING,
				UsageMessageSpec.SECTION_KEY_DESCRIPTION, UsageMessageSpec.SECTION_KEY_COMMAND_LIST_HEADING,
				UsageMessageSpec.SECTION_KEY_COMMAND_LIST, UsageMessageSpec.SECTION_KEY_FOOTER_HEADING,
				UsageMessageSpec.SECTION_KEY_FOOTER));
		System.exit(commandLine.execute(args));
	}

	@Override
	public Integer call() throws Exception {
		if (files == null || files.length == 0 || (files.length == 1 && "-".equals(files[0].getName()))) {
			System.out.println(DigestUtils.md5Hex(System.in) + "  -");
		} else {
			Stream.of(files).parallel().forEach(file -> {
				try (InputStream inputStream = new FileInputStream(file)) {
					System.out.printf("%s %s\n", DigestUtils.md5Hex(inputStream), file.getName());
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			});
		}
		return 0;
	}

}
