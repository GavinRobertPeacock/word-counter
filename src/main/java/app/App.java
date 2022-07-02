package app;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class App
{
	public static void main (String[] args)
	{
		if (args.length != 1)
		{
			System.out.print("Usage: java -jar word-counter-???.jar <text-file>");
			System.exit(1);
		}
		
		WordCounter counter = new WordCounter();
		try
		{
			Files.asCharSource(new File(args[0]), Charsets.UTF_8)
				.forEachLine(counter::processText);
		}
		catch (IOException ex)
		{
			System.out.println("Failed to read from source:");
			System.out.println(ex.getMessage());
			System.exit(2);
		}
		System.out.print(counter.generateReport());
	}
}
