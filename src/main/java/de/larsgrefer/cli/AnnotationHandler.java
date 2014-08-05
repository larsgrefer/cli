/*
 * The MIT License
 *
 * Copyright 2014 Lars Grefer.
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

import de.larsgrefer.cli.annotations.CliArgumentParser;
import de.larsgrefer.cli.annotations.CliOption;
import de.larsgrefer.cli.exceptions.ArgumentParserRequiredException;
import de.larsgrefer.cli.exceptions.DuplicateOptionException;
import de.larsgrefer.cli.exceptions.NoArgumentAllowedException;
import de.larsgrefer.cli.exceptions.ParserInstantiationException;
import de.larsgrefer.cli.model.CommandLineOption;
import de.larsgrefer.cli.model.ListOption;
import de.larsgrefer.cli.model.SimpleArgumentedOption;
import de.larsgrefer.cli.model.SimpleOption;
import de.larsgrefer.cli.parser.ArgumentParser;
import de.larsgrefer.cli.parser.ByteParser;
import de.larsgrefer.cli.parser.CharParser;
import de.larsgrefer.cli.parser.FileParser;
import de.larsgrefer.cli.parser.IntParser;
import de.larsgrefer.cli.parser.LongParser;
import de.larsgrefer.cli.parser.ShortParser;
import de.larsgrefer.cli.parser.StringParser;
import de.larsgrefer.cli.parser.UrlParser;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Lars Grefer
 */
public class AnnotationHandler {

	private Map<Type, ArgumentParser<?>> parserDatabase;
	private Logger log;

	public AnnotationHandler() {
		this(getDefaultParserDatabase());
	}

	public AnnotationHandler(Map<Type, ArgumentParser<?>> parserDatabase) {
		log = LogManager.getLogger(AnnotationHandler.class);
		this.parserDatabase = parserDatabase;
	}

	//<editor-fold defaultstate="collapsed" desc="Getter and Setter">
	public Map<Type, ArgumentParser<?>> getParserDatabase() {
		return parserDatabase;
	}

	public void setParserDatabase(Map<Type, ArgumentParser<?>> parserDatabase) {
		this.parserDatabase = parserDatabase;
	}

	//</editor-fold>
	public <T> void fillOptions(T object, String[] args) throws DuplicateOptionException, NoArgumentAllowedException, IllegalArgumentException, IllegalAccessException {
		log.entry(object, args);

		log.info("Generating Options from Annotations");
		List<CommandLineOption> opts = getOptions(object).collect(Collectors.toList());

		log.info("Fill the gernerated options with the given args");
		ArgsHandler<List<CommandLineOption>> argsHandler = new ArgsHandler<>(opts);
		argsHandler.fillOptionWithArgs(args);

		log.info("Fill the given object with the values of the filled Options");
		fillObject(object, argsHandler);
	}

	private <T> void fillObject(T obj, ArgsHandler<List<CommandLineOption>> argsHandler) throws IllegalAccessException, SecurityException, IllegalArgumentException {
		List<Field> fields = getAnnotatedFields(obj).collect(Collectors.toList());
		for (Field field : fields) {
			OptionName on = getOptionName(field);
			CommandLineOption option;
			if (on.name != CliOption.DEFAULT_NAME) {
				option = argsHandler.getOptionByName(on.name);
			} else {
				option = argsHandler.getOptionByLongName(on.longName);
			}
			Object value = option.getValue();
			if (value != null) {
				field.setAccessible(true);
				field.set(obj, value);
			}
		}
	}

	public Function<Object, Stream<CommandLineOption>> getOptions = obj -> {
		return getOptions(obj);
	};

	public static Function<Object, Stream<Field>> getAnnotatedFields = obj -> {
		return getAnnotatedFields(obj);
	};

	public static Stream<Field> getAnnotatedFields(Object obj) {
		Class<?> clazz = obj.getClass();

		Field[] declaredFields = clazz.getDeclaredFields();

		return Arrays.stream(declaredFields)
				.filter(field -> field.isAnnotationPresent(CliOption.class));
	}

	public Stream<CommandLineOption> getOptions(Object obj) {
		return getAnnotatedFields(obj).map(annotatedFieldToOption);
	}

	public Stream<CommandLineOption> getOptions(Object obj, Object obj2) {
		return Stream.concat(getOptions(obj), getOptions(obj2));
	}

	public Stream<CommandLineOption> getOptions(Object... objects) {
		return Arrays.stream(objects)
				.parallel()
				.flatMap(getOptions);
	}

	public Stream<CommandLineOption> getOptions(Collection<? extends Object> objects) {
		return objects
				.parallelStream()
				.flatMap(getOptions);
	}

	Function<Field, ? extends CommandLineOption> annotatedFieldToOption = field -> annotatedFieldToOption(field);

	CommandLineOption annotatedFieldToOption(Field field) {
		OptionName optionName = getOptionName(field);

		boolean required = field.getAnnotation(CliOption.class).required();

		String description = field.getAnnotation(CliOption.class).description();

		Class<?> fieldType = field.getType();

		ArgumentParser<?> parser = null;
		if (field.isAnnotationPresent(CliArgumentParser.class)) {
			log.debug("Found CliArgumentParser annotation at field {}", field.getName());
			try {
				parser = field.getAnnotation(CliArgumentParser.class).value().newInstance();
			} catch (InstantiationException | IllegalAccessException ex) {
				log.error("Could not obtain a new instance of Parser class {}", field.getAnnotation(CliArgumentParser.class).value().getName());
				throw log.throwing(new ParserInstantiationException(ex));
			}
		}

		if (parser == null && (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class))) {
			return log.exit(new SimpleOption(optionName.name, optionName.longName, required, description));
		}

		if (List.class.isAssignableFrom(fieldType)) {
			ParameterizedType type = (ParameterizedType) field.getGenericType();
			Type[] actualTypeArguments = type.getActualTypeArguments();
			if (parser != null) {
				return new ListOption(optionName.name, optionName.longName, required, description, parser);
			}
			parser = getParserDatabase().get(actualTypeArguments[0]);
			if (parser != null) {
				return log.exit(new ListOption(optionName.name, optionName.longName, required, description, parser));
			} else {
				log.error("Cannot automaticly parse Values of Type {} at Field {}", actualTypeArguments[0].getTypeName(), field.getName());
				throw log.throwing(new ArgumentParserRequiredException());
			}
		}

		return log.exit(new SimpleArgumentedOption(optionName.name, optionName.longName, required, description, getParser(field)));
	}

	ArgumentParser<?> getParser(Field field) {

		if (field.isAnnotationPresent(CliArgumentParser.class)) {

			try {
				return log.exit(field.getAnnotation(CliArgumentParser.class).value().newInstance());
			} catch (InstantiationException | IllegalAccessException ex) {
				throw log.throwing(new ParserInstantiationException(ex));
			}
		}

		Class<?> fieldType = field.getType();

		ArgumentParser<?> parser = classToParser(fieldType);

		if (parser != null) {
			return log.exit(parser);
		}

		throw log.throwing(
				new ArgumentParserRequiredException(
						"Cannot automaticly parse Values of Type "
						+ fieldType.getTypeName()
						+ " at Field "
						+ field.getName()
				)
		);
	}

	static OptionName getOptionName(Field field) {
		OptionName on = new OptionName();

		on.name = field.getAnnotation(CliOption.class).name();

		on.longName = field.getAnnotation(CliOption.class).longName();
		if (on.longName.equals(CliOption.DEFAULT_LONG_NAME)) {
			on.longName = field.getName();
		}

		return on;
	}

	static class OptionName {

		char name;
		String longName;
	}

	static Map<Type, ArgumentParser<?>> getDefaultParserDatabase() {
		Map<Type, ArgumentParser<?>> databaseMap = new HashMap<>();

		databaseMap.put(String.class, new StringParser());

		CharParser charParser = new CharParser();
		databaseMap.put(Character.class, charParser);
		databaseMap.put(char.class, charParser);

		ByteParser byteParser = new ByteParser();
		databaseMap.put(Byte.class, byteParser);
		databaseMap.put(byte.class, byteParser);

		ShortParser shortParser = new ShortParser();
		databaseMap.put(Short.class, shortParser);
		databaseMap.put(short.class, shortParser);

		IntParser intParser = new IntParser();
		databaseMap.put(Integer.class, intParser);
		databaseMap.put(int.class, intParser);

		LongParser longParser = new LongParser();
		databaseMap.put(Long.class, longParser);
		databaseMap.put(long.class, longParser);

		databaseMap.put(File.class, new FileParser());
		databaseMap.put(URL.class, new UrlParser());

		return databaseMap;
	}

	public <T> void registerParser(Class<T> type, ArgumentParser<T> parser) {
		getParserDatabase().put(type, parser);
	}

	public Function<Type, ArgumentParser<?>> typeToParser = type -> getParserDatabase().get(type);

	public <A> ArgumentParser<A> classToParser(Class<A> clazz) {
		return (ArgumentParser<A>) typeToParser.apply(clazz);
	}
}
