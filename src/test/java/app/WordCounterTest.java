package app;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class WordCounterTest
{
	private static final String TEST_LINE = "'Twas brillig -> and the --sl?thy-- toves,\r\n"
			+ "Did: gyre & \"gamble\" in-the **!*.";
	
	@Test
	void testWordExtraction()
	{
		final List<String> words = WordCounter.WORD_PATTERN.matcher(TEST_LINE).results()
			.map(result -> result.group(WordCounter.WORD_GROUP))
			.toList();
		
		assertEquals(
				List.of("Twas", "brillig", "and", "the", "sl?thy", "toves",
						"Did", "gyre", "&", "gamble", "in", "the"),
				words);
	}

	@Test
	void testExample()
	{
		final WordCounter counter = new WordCounter();
		counter.processText("Hello world & good morning. The date is 18/05/2016");
	
		assertEquals(9, counter.getCount());
		assertEquals(4.556, counter.getAverageLength(), 0.001);
		assertEquals(Set.of(1,2,3,4,5,7,10), counter.getLengths());
		int len = 1;
		for (int count : List.of(1,1,1,2,2,0,1,0,0,1))
		{
			assertEquals("Count must match for length: " + len, count, counter.getLengthCount(len));
			++len;
		}
		assertEquals(List.of(4,5), counter.getMostFrequentLengths());
		
		final String expected = "Word count = 9\n"
			+ "Average word length = 4.556\n"
			+ "Number of words of length 1 is 1\n"
			+ "Number of words of length 2 is 1\n"
			+ "Number of words of length 3 is 1\n"
			+ "Number of words of length 4 is 2\n"
			+ "Number of words of length 5 is 2\n"
			+ "Number of words of length 7 is 1\n"
			+ "Number of words of length 10 is 1\n"
			+ "The most frequently occurring word length is 2, for word lengths of 4 & 5\n";
		
		assertEquals(
				expected.replaceAll("\n", System.lineSeparator()),
				counter.generateReport());
	}
	
	@Test
	public void testNullReport()
	{
		final WordCounter counter = new WordCounter();
		counter.processText("**** ?!! ****");

		assertEquals(0, counter.getCount());
		assertEquals(Set.of(), counter.getLengths());
		assertEquals(List.of(), counter.getMostFrequentLengths());
		
		assertThrows(IllegalStateException.class, () -> {
	        counter.getAverageLength();
	    });
		
		assertTrue(counter.generateReport().contains("No words"));
	}
}
