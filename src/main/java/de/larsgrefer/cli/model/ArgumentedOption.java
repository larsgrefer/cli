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

package de.larsgrefer.cli.model;

import com.google.common.base.Objects;
import de.larsgrefer.cli.parser.ArgumentParser;

/**
 *
 * @author Lars Grefer
 * @param <I>
 * @param <O>
 */
public abstract class ArgumentedOption<I,O> extends CommandLineOption<O> {

	protected O value;

	ArgumentParser<I> parser;

	public ArgumentedOption() {
	}

	public ArgumentedOption( char name, String longName, boolean required, String description, ArgumentParser<I> parser) {
		super(name, longName, required, description);
		this.parser = parser;
	}
	
	@Override
	public O getValue() {
		return isSet() ? value : null;
	}

	public void setValue(O newValue) {
		this.value = newValue;
	}

	public ArgumentParser<I> getParser() {
		return parser;
	}

	public void setParser(ArgumentParser<I> parser) {
		this.parser = parser;
	}

	public abstract void addValue(String valueString);

	@Override
	protected Objects.ToStringHelper getToStringHelper() {
		return super.getToStringHelper()
				.add("value", value)
				.add("parser", parser);
	}
	
	
}
