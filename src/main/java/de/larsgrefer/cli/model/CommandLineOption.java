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

/**
 *
 * base class for all command line options
 * 
 * @author Lars Grefer
 * @param <T>
 */
public abstract class CommandLineOption<T> implements Comparable<CommandLineOption<T>>{

	public CommandLineOption() {
		description = "";
	}

	public CommandLineOption(char name, String longName, boolean required, String description) {
		this.name = name;
		this.longName = longName;
		this.required = required;
		this.description = description;
	}
	
	private char name;
	private String longName;
	private boolean required;
	private String description;
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

	public String getDescription() {
		return description;
	}
	
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

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean hasName()
	{
		return name != '\0';
	}
	
	public boolean hasLongName()
	{
		return longName != null && !longName.isEmpty();
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
		return getToStringHelper().toString();
	}

	@Override
	public int compareTo(CommandLineOption<T> other) {
		if(this.name != '\0' && other.name != '\0')
			return Character.toString(name).compareTo(Character.toString(other.name));
		else if(this.longName != null && !this.longName.isEmpty() && other.longName != null && !other.longName.isEmpty())
			return this.longName.compareToIgnoreCase(other.longName);
		return 0;
	}
}
