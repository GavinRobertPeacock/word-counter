package app;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * A word counting utility.
 * To use, create an instance of the class,
 * Call processText() multiple times,
 * Then check the statistics access functions or call generateReport().
 * 
 * Words are delimited by whitespace and parsed to remove leading and trailing syntax characters.
 * Whitespace includes hyphen so that hyphenated words get split.
 * A word must contain a word character: alpha-numeric or ampersand; 
 * Any character that is not a word character is considered syntax.
 * 
 * This class is not thread-safe - use as you would a builder class.
 * 
 * @author Gavin Peacock
 */
public final class WordCounter
{
	/*
	 * This pattern defines a word as follows:
	 * [^a-zA-Z0-9\\s&]*		any leading syntax character to be discarded
	 * ([^\\s-]*[a-zA-Z0-9&])	a capture group for the word characters
	 * [^a-zA-Z0-9\\s&]*		any trailing syntax characters to be discarded
	 * ([\\s-]+|$)				a delimiting by space or end of text
	 * */
	@VisibleForTesting
	static final Pattern WORD_PATTERN = Pattern.compile(
			"[^a-zA-Z0-9\\s&]*([^\\s-]*[a-zA-Z0-9&])[^a-zA-Z0-9\\s&]*([\\s-]+|$)");
	@VisibleForTesting
	static int WORD_GROUP = 1;
	
	private int count_ = 0;
	private int totalLength_ = 0;
	private SortedMap<Integer,Integer> lengthCounts_ = new TreeMap<>();

	/**
	 * Call this method multiple times to add in all text to be counted.
	 * 
	 * @param text for counting, not null
	 */
	public void processText(final String text)
	{
		checkNotNull(text, "A text object must be provided");
		
		final Matcher matcher = WORD_PATTERN.matcher(text);
		matcher.results().forEach(result ->
		{
			final String word = result.group(WORD_GROUP);
			++count_;
			final int len = word.length();
			totalLength_ += len;
			lengthCounts_.put(len, lengthCounts_.getOrDefault(len,0) + 1);
		});
	}

	/**
	 * @return the total number of word counted
	 */
	public int getCount()
	{
		return count_;
	}

	/**
	 * @return the average length of the words
	 * @throws IllegalStateException if GetCount() is zero
	 */
	public double getAverageLength()
	{
		checkState(count_>0, "Average cannot be calculated when no words have been found");
		return (double)totalLength_ / count_;
	}
	
	/**
	 * @return an ordered set of word lengths, not null but maybe empty
	 */
	public Set<Integer> getLengths()
	{
		return lengthCounts_.keySet();
	}

	/**
	 * Get the count of words of the given length.
	 * 
	 * @param length of the word
	 * @return the count, maybe zero if not found in getLengths()
	 */
	public int getLengthCount(int length)
	{
		return lengthCounts_.containsKey(length) ? lengthCounts_.get(length) : 0;
	}
	
	/**
	 * @return the maximum word length, zero only if getCount() is zero
	 */
	public int getMaxLengthCount()
	{
		return lengthCounts_.values().stream().reduce(0, Math::max);
	}
	
	/**
	 * @return a list of word lengths that are the most common found, not null, empty only if getCount() zero
	 */
	public List<Integer> getMostFrequentLengths()
	{
		final int maxCount = getMaxLengthCount();
		return lengthCounts_.entrySet().stream()
				.filter(entry -> entry.getValue() == maxCount)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	/**
	 * Generates a report that collates these results.
	 * Newlines are adjusted for platform.
	 * The report ends with a newline.
	 * 
	 * @return the report, not null or empty
	 */
	public String generateReport()
	{
		if (getCount()==0)
		{
			return "No words have been found" + System.lineSeparator();
		}
		final StringBuilder report = new StringBuilder();
		
		report.append("Word count = %d\n".formatted(getCount()));
		report.append("Average word length = %.3f\n".formatted(getAverageLength()));
		for (int len : getLengths())
		{
			report.append("Number of words of length %d is %d\n".formatted(len, getLengthCount(len)));
		}
		report.append("The most frequently occurring word length is %d, for word lengths of "
				.formatted(getMaxLengthCount()));
		final List<Integer> lengths = getMostFrequentLengths();
		if (lengths.size() > 1)
		{
			Joiner.on(", ").appendTo(report, Iterables.limit(lengths,lengths.size()-1));
			report.append(" & ");
		}
		// There will always be at least one entry.
		report.append(lengths.get(lengths.size()-1)).append("\n");

		return report.toString().replaceAll("\\n", System.lineSeparator());
	}
}
