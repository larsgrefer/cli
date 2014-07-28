/*
 * The MIT License
 *
 * Copyright 2014 lgrefer.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.larsgrefer.cli;

import de.larsgrefer.cli.exceptions.DuplicateOptionException;
import de.larsgrefer.cli.exceptions.NoArgumentAllowedException;
import de.larsgrefer.cli.model.ArgumentedOption;
import de.larsgrefer.cli.model.CommandLineOption;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author lgrefer
 * @param <T>
 */
public class ArgsHandler<T extends Collection<? extends CommandLineOption>> {

	private T options;
	private final Map<Character, CommandLineOption> optionsByName;
	private final Map<String, CommandLineOption> optionsByLongName;

	public ArgsHandler(T options) throws DuplicateOptionException {
		optionsByName = new TreeMap<>();
		optionsByLongName = new TreeMap<>();

		for (CommandLineOption opt : options) {
			if (optionsByName.containsKey(opt.getName())) {
				throwNewDuplicateByNameException(opt);
			}
			optionsByName.put(opt.getName(), opt);

			if (optionsByLongName.containsKey(opt.getLongName())) {
				throwNewDuplicateByLongNameException(opt);
			}
			optionsByLongName.put(opt.getLongName(), opt);
		}
	}

	private String[] args;

	public synchronized T fillOptionWithArgs(String[] args) throws NoArgumentAllowedException {
		this.args = args;

		int[] optionStarts
				= IntStream.concat(
						IntStream
						.range(0, args.length)
						.filter(i -> args[i].startsWith("-")),
						IntStream.of(args.length))
				.toArray();

		for (int i = 0; i < optionStarts.length - 1; i++) {
			int a = optionStarts[i];
			int b = optionStarts[i + 1];
			if (args[a].startsWith("--")) {
				fillLongNamedOption(a, b);
			} else {
				fillShortNamedOption(a, b);
			}
		}

		return getOptions();

	}

	private void throwNewDuplicateByNameException(CommandLineOption opt) throws DuplicateOptionException {
		String dop = getOptions().stream()
				.filter(option -> option.getName() == opt.getName())
				.map(option -> option.toString())
				.collect(Collectors.joining("\n"));
		throw new DuplicateOptionException("The following Options have duplicate Names:\n" + dop);
	}

	private void throwNewDuplicateByLongNameException(CommandLineOption opt) throws DuplicateOptionException {
		String dop = getOptions().stream()
				.filter(option -> option.getLongName().equals(opt.getLongName()))
				.map(option -> option.toString())
				.collect(Collectors.joining("\n"));
		throw new DuplicateOptionException("The following Options have duplicate LongNames:\n" + dop);
	}

	private void fillLongNamedOption(int argIndex, int nextArgIndex) throws NoArgumentAllowedException {
		CommandLineOption option = getOptionsByLongName().get(args[argIndex].substring(2));
		fillOption(option, argIndex, nextArgIndex);
	}

	private void fillShortNamedOption(int argIndex, int nextArgIndex) throws NoArgumentAllowedException {
		char[] optionChars = args[argIndex].substring(1).toCharArray();
		for (int i = 0; i < optionChars.length - 1; i++) {
			CommandLineOption option = getOptionsByName().get(optionChars[i]);
			fillOption(option, argIndex, argIndex);
		}
		CommandLineOption option = getOptionsByName().get(optionChars[optionChars.length - 1]);
		fillOption(option, argIndex, nextArgIndex);
	}

	private void fillOption(CommandLineOption option, int argIndex, int nextArgIndex) throws NoArgumentAllowedException {
		if (option == null) {
			return;
		}
		option.setSet(true);
		if (argIndex + 1 < nextArgIndex) //we have arguments given
		{
			if (option instanceof ArgumentedOption) {
				ArgumentedOption ao = (ArgumentedOption) option;
				IntStream.range(argIndex + 1, nextArgIndex).forEach(i -> ao.addValue(args[i]));
			} else {
				throw new NoArgumentAllowedException("No arguments allowed for Option " + args[argIndex]);
			}
		}
	}

	public T getOptions() {
		return options;
	}

	public Map<Character, CommandLineOption> getOptionsByName() {
		return optionsByName;
	}

	public CommandLineOption getOptionByName(char name) {
		return getOptionsByName().get(name);
	}

	public Map<String, CommandLineOption> getOptionsByLongName() {
		return optionsByLongName;
	}
	
	public CommandLineOption getOptionByLongName(String name) {
		return getOptionsByLongName().get(name);
	}
}
