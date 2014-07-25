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

import com.google.common.base.Objects;

/**
 *
 * @author lgrefer
 * @param <T>
 */
public abstract class Option<T> {

	public Option() {
	}

	public Option(char name, String longName, boolean required) {
		this.name = name;
		this.longName = longName;
		this.required = required;
	}
	
	private char name;
	
	private String longName;
	
	private boolean required;

	private boolean set;
	
	public char getName() {
		return name;
	}

	public String getLongName() {
		return longName;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isSet() {
		return set;
	}
	
	public abstract T getValue();

	public void setName(char name) {
		this.name = name;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public void setSet(boolean set) {
		this.set = set;
	}
	
	protected Objects.ToStringHelper getToStringHelper()
	{
		return Objects.toStringHelper(this)
				.add("name", name)
				.add("longName", longName)
				.add("required", required)
				.add("set", set);
	}

	@Override
	public String toString() {
		return getToStringHelper().omitNullValues().toString();
	}
	
	
}
