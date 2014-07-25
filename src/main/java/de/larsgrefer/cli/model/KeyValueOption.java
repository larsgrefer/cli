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

package de.larsgrefer.cli.model;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

/**
 *
 * @author lgrefer
 * @param <K>
 * @param <V>
 */
public abstract class KeyValueOption<K,V> extends ArgumentedOption<String,Map<K,V>>{

	public KeyValueOption(Function<String, K> keyParser, Function<String, V> valueParser) {
		
		this.keyParser = keyParser;
		this.valueParser = valueParser;
		setValue(new TreeMap<>());
	}
	
	Function<String, K> keyParser;
	Function<String, V> valueParser;
	char separator = '=';
	
	@Override
	public void addValue(String s)
	{
		int index = s.indexOf(separator);
		K newKey = keyParser.apply(s.substring(0, index));
		V newValue = valueParser.apply(s.substring(index+1));
		getValue().put(newKey, newValue);
	}
}
