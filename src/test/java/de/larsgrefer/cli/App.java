package de.larsgrefer.cli;

import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
		Arrays.stream(args).forEach(System.out::println);
    }
}
