Read Me - word counter

Introduction
------------

This utility is a simple demonstration of reading in a text file and counting its words.
In addition to a global count, word lengths are calculated and the frequency of each word length compiled.
This information is then reported in stdout.

The text is tokenised according to the presence of whitespace and hyphens.
Hyphens are treated as whitespace so that strings of hyphened adjectives are counted as separate words.
Each token is then stripped of extraneous syntax characters and the remaining word characters counted to obtain
lengths.  This prevents "quoted sentences" --emphasised-- words, trailing commas and full stops from being
counted in the length of each word.  Syntax within a word such as 14/6/67 is counted as part of the word.
If no word characters are found then no word is recorded in the count.
A word character must be either alpha-numeric or an ampersand.  All other characters constitute syntax.
The ampersand character is included as it is regularly used as a substitute for the word "and" and
less regularly used in contractions such as &c. and &al.

The counter makes no attempt to match up words and so no count is made based on unique word lengths.
This is in keeping with the head line count which gives the overall count of words used
regardless of uniqueness.
No handling is provided for end-of-line hyphenation for broken words.  In principle, this can easily be taken
care of, however, this use case has become extremely rare and any such implementation is likely to fall foul
of other, more common use cases of hyphens.

The interpretation what constitutes a word is highly subjective.  To produce a fully robust parser is likely
to be impossible as conflicting use cases probably exist.  To evaluate what would make an optimally robust
parser requires a compilation of syntax use cases and their frequency of use.  This is prohibitive.  The
approach taken here is to concentrate on what makes a good word character rather than trying to model all
cases of syntax use.  The reason is that the word characters are likely to be a more stable measure.
Nevertheless, there are some spurious corner cases, particularly with the apostrophe.  So, for example, the
Shakespearian 'Twas is parsed as Twas, and plural possession omits the trailing apostrophe.  It is highly
debatable whether this is right or wrong.  Another corner case is that && is treated as a word.  This is because
ampersand is classified as a word character.  It is possible to tweak the matching pattern to exclude this,
but as it is not clear what the use cases of && will be, this is regarded as an optimisation too far.
There is also the use of the full stop in contractions such as etc. which should ideally be counted with the
word.  This could be done using a dictionary of contractions, but has not been.

Build
-----

To build, run the Maven update project then run the Maven Build "Package" configuration.

Installation
------------

To install, copy the file:
   word counter/target/word-counter-0.0.1.jar
to a suitable installation folder.
This jar has the external dependencies added for the sake of convenience.

Execution
---------

Execute using the command:
   java -jar <installation-dir>/word-counter-0.0.1.jar <text-file>
where:
   You will need to substitute the full path for "java" if not on your system path.
   <installation-dir> is the location of the copied JAR file.
   <text-file> is the path (resolved against the working directory) to the text file to be read.
