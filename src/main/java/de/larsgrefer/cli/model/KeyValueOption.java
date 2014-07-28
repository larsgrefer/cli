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
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 *
 * @author Lars Grefer
 * @param <K>
 * @param <V>
 */
public abstract class KeyValueOption<K,V> extends ArgumentedOption<String,Map<K,V>>{

	private ArgumentParser<K> keyParser;
	private ArgumentParser<V> valueParser;
	private Supplier<Map<K, V>> newMapSupplier = () -> new TreeMap<>();
	private char separator = '=';

	public KeyValueOption() {
	}

	public KeyValueOption(char name, String longName, boolean required, String description, ArgumentParser<K> keyParser, ArgumentParser<V> valueParser) {
		super(name, longName, required, description, null);
		this.keyParser = keyParser;
		this.valueParser = valueParser;
	}
	
	public KeyValueOption(char name, String longName, boolean required, String description, ArgumentParser<K> keyParser, ArgumentParser<V> valueParser, Supplier<Map<K, V>> newMapSupplier) {
		super(name, longName, required, description, null);
		this.keyParser = keyParser;
		this.valueParser = valueParser;
		this.newMapSupplier = newMapSupplier;
	}
	
	public ArgumentParser<K> getKeyParser() {
		return keyParser;
	}

	public void setKeyParser(ArgumentParser<K> keyParser) {
		this.keyParser = keyParser;
	}

	public ArgumentParser<V> getValueParser() {
		return valueParser;
	}

	public void setValueParser(ArgumentParser<V> valueParser) {
		this.valueParser = valueParser;
	}

	public Supplier<Map<K, V>> getNewMapSupplier() {
		return newMapSupplier;
	}

	public void setNewMapSupplier(Supplier<Map<K, V>> newMapSupplier) {
		this.newMapSupplier = newMapSupplier;
	}

	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}
	
	@Override
	public void addValue(String s)
	{
		if(value == null)
			value = newMapSupplier.get();
		int index = s.indexOf(separator);
		K newKey = keyParser.apply(s.substring(0, index));
		V newValue = valueParser.apply(s.substring(index+1));
		value.put(newKey, newValue);
	}

	@Override
	protected Objects.ToStringHelper getToStringHelper() {
		return super.getToStringHelper()
				.add("separator", separator)
				.add("keyParser", keyParser)
				.add("valueParser", valueParser)
				.add("newMapSupplier", newMapSupplier);
	}

	
	
	@Override
	public ArgumentParser<String> getParser() {
		throw new UnsupportedOperationException("getParser() is not used by KeyValueOption. Use getKeyParser() and getValueParser() instead");
	}

	@Override
	public void setParser(ArgumentParser<String> parser) {
		throw new UnsupportedOperationException("setParser() is not used by KeyValueOption. Use setKeyParser() and setValueParser() instead");
	}
	
}
