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

import de.larsgrefer.cli.annotations.CliArgumentParser;
import de.larsgrefer.cli.annotations.CliOption;
import de.larsgrefer.cli.exceptions.ArgumentParserRequiredException;
import de.larsgrefer.cli.exceptions.DuplicateOptionException;
import de.larsgrefer.cli.exceptions.NoArgumentAllowedException;
import de.larsgrefer.cli.exceptions.ParserInstantiationException;
import de.larsgrefer.cli.model.ListOption;
import de.larsgrefer.cli.model.Option;
import de.larsgrefer.cli.model.SimpleArgumentedOption;
import de.larsgrefer.cli.model.SimpleOption;
import de.larsgrefer.cli.parser.ByteParser;
import de.larsgrefer.cli.parser.CharParser;
import de.larsgrefer.cli.parser.ICliParser;
import de.larsgrefer.cli.parser.IntParser;
import de.larsgrefer.cli.parser.StringParser;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author lgrefer
 */
public class AnnotationHandler {

	public static Function<Object, Stream<Option>> getOptions = obj -> {
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

	public static Stream<Option> getOptions(Object obj) {
		return getAnnotatedFields(obj).map(annotationOptionMapper);
	}

	public static Stream<Option> getOptions(Object obj, Object obj2) {
		return Stream.concat(getOptions(obj), getOptions(obj2));
	}

	public static Stream<Option> getOptions(Object... objects) {
		return Arrays.stream(objects)
				.parallel()
				.flatMap(getOptions);
	}

	public static Stream<Option> getOptions(Collection<? extends Object> objects) {
		return objects
				.parallelStream()
				.flatMap(getOptions);
	}

	public static <T> void fillOptions(T obj, String[] args) throws DuplicateOptionException, NoArgumentAllowedException, IllegalArgumentException, IllegalAccessException {
		List<Option> opts = getOptions(obj).collect(Collectors.toList());
		ArgsHandler<List<Option>> argsHandler = new ArgsHandler<>(opts);
		argsHandler.fillOptionWithArgs(args);
		List<Field> fields = getAnnotatedFields(obj).collect(Collectors.toList());
		for (Field field : fields) {
			OptionName on = getOptionName(field);
			Option option;
			if (on.name != CliOption.DEFAULT_NAME) {
				option = argsHandler.optionsByName.get(on.name);
			} else {
				option = argsHandler.optionsByLongName.get(on.longName);
			}
			Object value = option.getValue();
			if(value != null)
				field.set(obj, value);
		}
	}

	static Function<Field, ? extends Option> annotationOptionMapper = field -> {
		OptionName optionName = getOptionName(field);

		boolean required = field.getAnnotation(CliOption.class).required();

		Class<?> fieldType = field.getType();

		Function<String, ?> parser = null;
		if (field.isAnnotationPresent(CliArgumentParser.class)) {

			try {
				parser = field.getAnnotation(CliArgumentParser.class).value().newInstance();
			} catch (InstantiationException | IllegalAccessException ex) {
				throw new ParserInstantiationException(ex);
			}
		}

		if (parser == null && (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class))) {
			return new SimpleOption(optionName.name, optionName.longName, required);
		}

		if (List.class.isAssignableFrom(fieldType)) {
			ParameterizedType type = (ParameterizedType) field.getGenericType();
			Type[] actualTypeArguments = type.getActualTypeArguments();
			if (parser != null) {
				return new ListOption(optionName.name, optionName.longName, required, parser);
			}
			parser = getParser(actualTypeArguments[0]);
			if (parser != null) {
				return new ListOption(optionName.name, optionName.longName, required, parser);
			} else {
				throw new ArgumentParserRequiredException("Cannot automaticly parse Values of Type " + actualTypeArguments[0].getTypeName() + " at Field " + field.getName());
			}
		}

		return new SimpleArgumentedOption(optionName.name, optionName.longName, required, getParser(field));

	};

	static ICliParser<?> getParser(Field field) {

		if (field.isAnnotationPresent(CliArgumentParser.class)) {

			try {
				return field.getAnnotation(CliArgumentParser.class).value().newInstance();
			} catch (InstantiationException | IllegalAccessException ex) {
				throw new ParserInstantiationException(ex);
			}
		}

		Class<?> fieldType = field.getType();

		ICliParser<?> parser = getParser(fieldType);

		if (parser != null) {
			return parser;
		}

		throw new ArgumentParserRequiredException("Cannot automaticly parse Values of Type " + fieldType.getTypeName() + " at Field " + field.getName());
	}

	static ICliParser<?> getParser(Type type) {

		if (type.equals(String.class)) {
			return new StringParser();
		}

		if (type.equals(Byte.class) || type.equals(byte.class)) {
			return new ByteParser();
		}
		
		if (type.equals(Short.class) || type.equals(short.class)) {
			return new IntParser();
		}
		
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return new IntParser();
		}
		
		if (type.equals(Long.class) || type.equals(long.class)) {
			return new IntParser();
		}

		if (type.equals(Character.class) || type.equals(char.class)) {
			return new CharParser();
		}
		
		return null;
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

}
