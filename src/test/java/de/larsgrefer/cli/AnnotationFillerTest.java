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

import com.google.common.base.Objects;
import de.larsgrefer.cli.annotations.CliOption;
import de.larsgrefer.cli.exceptions.DuplicateOptionException;
import de.larsgrefer.cli.exceptions.NoArgumentAllowedException;
import de.larsgrefer.cli.model.CommandLineOption;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author lgrefer
 */
public class AnnotationFillerTest {

	public AnnotationFillerTest() {
	}

	@Test
	public void test() throws DuplicateOptionException, NoArgumentAllowedException {
		TestClass tc = new TestClass();
		AnnotationHandler anh = new AnnotationHandler();
		List<CommandLineOption> opts = anh.getOptions(tc).collect(Collectors.toList());

		opts.forEach(System.out::println);

		assertTrue(opts.stream().anyMatch(o -> o.getName() == 'a'));
		assertTrue(opts.stream().anyMatch(o -> o.getName() == 'b'));
		assertTrue(opts.stream().anyMatch(o -> o.getName() == 'c'));

		ArgsHandler<List<CommandLineOption>> ah = new ArgsHandler(opts);

		ah.fillOptionWithArgs(new String[]{"-ac", "-b", "50", "--hallo", "x", "y", "z"});

		opts.forEach(System.out::println);
	}

	@Test
	public void test2() throws DuplicateOptionException, NoArgumentAllowedException, IllegalArgumentException, IllegalAccessException {
		TestClass tc = new TestClass();
		AnnotationHandler ah = new AnnotationHandler();
		ah.fillOptions(tc, new String[]{"-ac", "-b", "50", "--hallo", "x", "y", "z", "-e", "foo", "bar"});

		System.out.println(tc);

		assertEquals("foo", tc.etest.get(0));
		assertEquals("bar", tc.etest.get(1));
	}

	@Test
	public void test3() {
		try {
			AnnotationHandler ah = new AnnotationHandler();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public class TestClass {

		@CliOption(name = 'a')
		public boolean atest;

		@CliOption(name = 'b')
		public int btest;

		@CliOption(name = 'c')
		boolean ctest;

		@CliOption(name = 'd')
		char dtest;

		@CliOption(name = 'e')
		List<String> etest;

		@CliOption(name = 'f', longName = "hallo")
		List<Character> ftest;

		@Override
		public String toString() {
			return Objects.toStringHelper(this)
					.add("atest", atest)
					.add("btest", btest)
					.add("ctest", ctest)
					.add("dtest", dtest)
					.add("etest", etest)
					.add("ftest", ftest)
					.toString();
		}

	}
}
