<#include "include/head.tpl"/>
<div id="content" style="width: 600px; float: left;">

<h1>Syntax</h1>

The basic syntax of the Q shell is as follows (* = zero or more):
<pre>[command] (-[flag] [argument]*)* | [piped contents]</pre>




<h2>Commands</h2>

<code>:new -d `/tmp` `dir`</code><br/>
<p>
	Creates a new directory "/tmp/dir". I will use the existing plugins here in examples, but won't explain them fully.
	All plugins, their flags and their arguments are documented and can be found using the navigation menu on the left.
</p>

<p>
	The status line shows "new -directory `/tmp` `dir`" when you type this command.
	This shows that the -d flag is interpreted as the directory flag.
	Watch the status line, because it gives a lot of information on how a command is interpreted.
</p>

<code>:new-d`/tmp``dir`</code><br/>
<p>
	Once again, the same example with all spaces removed to show that whitespace is not significant.
	(Except for in String literals)
</p>

<h2>Flags</h2>
<p>Flags are always a single letter, disregard case and are marked by a single dash.</p>

<code>:find -i `pre*` -l `/tmp`</code><br/>
<p>
	Multiple flags can be specified. Here the -i and the -l flags are specified, with their arguments.
	The example above searches for all files and directories starting with "pre" in the "/tmp" folder .
</p>

<code>:find -il `pre*` `/tmp`</code><br/>
<p>The exact same example, with the flags combined. Every character after the dash is a new flag.</p>

<code>:find -li `/tmp` `pre*`</code><br/>
<p>
	Once again, the exact same command.
	Remember to order the arguments differently depending on the flag order.
	Arguments are accepted in the order of the flags, each flag accepts arguments until it has enough.
</p>

<code>:find `/tmp` `pre*` -li</code><br/>
<p>Not recommended, but works (once again, the same command).</p>

<p>
	For readability it is best to specify each flag individually and then the arguments.
	In this document I will always do this and make the arguments as explicit as possible for clarity.
	The status line always groups the arguments with their flags, so check the status line to catch mistakes.
</p>




<h2>Strings and Escape Sequences</h2>
<p>
So now I have discussed commands and flags, but I still need to discuss strings to complete the basic syntax.
As you have seen, arguments specified as string literals are started and closed with the back tick character '`'.
In Q you are required to escape the back tick '`' and the back slash '\' characters with '\q' and '\b' respectively.
(q for quote and b for back slash).
</p>

<code>:find -i `*\b*` -l `/tmp`</code><br/>
<p>
	Finds all files and directories nested in "/tmp" containing a back slash '\' in the name (there will be none).
</p>

<code>:find -i `*\q*` -l `/tmp`</code><br/>
<p>
	Finds all files and directories nested in "/tmp" containing a back tick '`' in the name.
</p>

<p>
	However, that is not all there is to be said about escape sequences.
	Q can access locations using multiple protocols and URL's are used to find these locations.
	URL's specify their own set of escape sequences.
	Every escape character is escaped with a percentile '%' and two other characters.
	For example, spaces ' ' are escaped with "%20" and the percentile '%' is escaped with '%25'.
	For a full listing, please google "URL escape sequences".
	When files or parts of files are expected, Q will require you to provide an escaped URL.
	This isn't much of a problem in practice,
	because you'd typically use variables rather than typing in the URL manually.
	This section, however, is included for completeness.
</p>

<code>with%20space</code><br/>
<p>
	Opens the directory "with space".
</p>

<p>
	It would be rather lame if users were required to know all URL escape characters by heart,
	in order to navigate the file system.
</p>

<code>with space</code><br/>
<p>Also works and does the exact same thing.</p>

<p>
	However, things don't necessarily work out this nicely.
	In Q, spaces are often used as a separator between files or locations.
</p>

<code>:move -u `/tmp/with%20space /tmp/withoutspace` `/tmp/b`</code><br/>
<p>
	Shows how spaces are used to separate files.
	The command above copies the two files "/tmp/with space" and "/tmp/withoutspace" into the "/tmp/b" directory.
</p>

<code>:move -u `/tmp/with space /tmp/withoutspace` `/tmp/b`</code><br/>
<p>
	Won't work! This command would attempt to move 3 files: "/tmp/with", "space" and "/tmp/withoutspace".
	This specific problem only occurs if multiple arguments are possible.
</p>

<code>%25</code><br/>
<p>
	Open opens the directory named "%".
</p>

<code>%</code><br/>
<p>
	Not the same! The percentile is a special case that always has to be escaped.
	Since URL escapes always start with a percentile '%', Q assumes that the percentile always signals an escape sequence.
	Otherwise, how would the application know if a percentile should be taken literally or not?
</p>

<p>
	Summarizing, when you manually specify URL's, properly escaped URL's with forward slashes never cause problems,
	because the Q escape characters are escaped in URL's.
	When browsing locations on the command line remember to always escape '%' with '%25',
	'`' with '\q' (the q escape sequence) or '%60' (the URL escape sequence) and '\' with '/'.
	For completeness: When an argument can accept more than one file you have to escape spaces ' ' with "%20".
</p>




<h2>piped content</h2>

<p>
	Some of the Q commands accept piped content.
	Following a bar '|' the complete line is piped as an argument.
	So after a bar '|' you can no longer add any flags, strings, etc.
</p>

<code>:new -f `/tmp` `tale.txt` | A tale about \` and friends.</code><br/>

<p>
	Will create a new file with the contents: "A tale about \` and friends.".
	If the contents of the file were passed as a string literal, this would become "A tale about \b\q and friends.".
	This is less obvious and more work. The main reasons I introduced pipes, are readability and usability.
</p>

<code>:find -l `/tmp` |.*\(\d+\).*</code><br/>
<p>
	Looks for any files or directories with a number between parentheses in the name.
	Escaping the back slashes here would make the expression more cryptic than it already is.
	Regular expressions passed to the find command can always be written in pure regex form.
</p>




<h2>Variables</h2>

<p>
	As I mentioned before, typing in locations manually is a lot of work, and this work is unnecessary.
	There is actually an alternative way to supply locations to the shell: variables.
</p>

<p>
	Variables refer to the current context and can be used to refer to the open directory, active selection, etc.
	Variables start with a dollar '$' have a base name and an optional suffix.
	The name defines what should be selected, and the suffix selects the left or right file list to take the selection from.
</p>

<table>
	<caption>Variable Names</caption>
	<tr><th>Variable</th><th>Description</th></tr>
	<tr><td>$url</td><td>The first selected file (or rather, the URL)</td></tr>
	<tr><td>$name</td><td>The first selected file's name only</td></tr>
	<tr><td>$dir</td><td>The currently open directory (as a URL)</td></tr>
	<tr><td>$parent</td><td>Not the open directory, but it's parent (as a URL)</td></tr>
	<tr><td>$urls</td><td>All selected files (or rather, the URL's)</td></tr>
	<tr><td>$names</td><td>All selected files, but their names only</td></tr>
</table>

<p>
	As you can see, the variables ending with an s are plural.
	The variable names above work and refer to files in the active file list.
</p>

<p>
	There is always at least one file selected in a Q file list, except for one special case: empty directories.
	In this situation $url, $name, $urls and $names will refer to the current directory (in other words to $dir).
</p>


<p>The possible suffixes:</p>

<ul>
	<li>no suffix: refer to the active file list.</li>
	<li>tilde suffix: '~' refer to the inactive file list.</li>
	<li>one '1' refer to the left file list (regardless of which is active).</li>
	<li>one '2' refer to the right file list (regardless of which is active).</li>
</ul>

<table>
	<caption>Variables Available in Q</caption>
	<tr> <th></th>                      <th>Active</th>  <th>Inactive</th> <th>Left</th>     <th>Right</th>   </tr>
	<tr> <td>Selected File</td>         <td>$url</td>    <td>$url~</td>    <td>$url1</td>    <td>$url2</td>    </tr>
	<tr> <td>Selected File's Name</td>  <td>$name</td>   <td>$name~</td>   <td>$name1</td>   <td>$name2</td>   </tr>
	<tr> <td>Current Directory</td>     <td>$dir</td>    <td>$dir~</td>    <td>$dir1</td>    <td>$dir2</td>    </tr>
	<tr> <td>Parent Directory</td>      <td>$parent</td> <td>$parent~</td> <td>$parent1</td> <td>$parent2</td> </tr>
	<tr> <td>Selected Files</td>        <td>$urls</td>   <td>$urls~</td>   <td>$urls1</td>   <td>$urls2</td>   </tr>
	<tr> <td>Selected Files' Names</td> <td>$names</td>  <td>$names~</td>  <td>$names1</td>  <td>$names2</td>  </tr>
</table>

<code>:copy -u $urls1 $dir2</code><br/>
<p>Copy all the files selected in the left file list to the directory open in the right file list.</p>

<code>:copy -s $url $dir `backup.copy`</code><br/>
<p>Clone the selected file in the active directory and name it "backup.copy"; </p>




<h2>How to save yourself some typing</h2>

<p>
	I have now discussed all the basics of the Q command line, but there are still some tricks I'd like to mention.
	The rest of this section shows how to achieve the same commands with less typing.
</p>

<code>:copy -u $urls $dir~</code><br/>
<p>
	The example above copies the selected files and directories from the active file list to the inactive file list.
	But there is a method to achieve the same, with far less typing:
</p>

<code>:c</code><br/>
<p>
</p>
The operation performed is identical.

<p>
	So how does this work?
	First of all, all plugins in Q have a unique starting letter, so the first letter is sufficient to find the command.
	The second trick is in defaults. In Q all plugins have a set of default flags, that are on by default if nothing is specified.
	In the case of the copy plugin, the url flag is on by default, so no need to specify it.
	At the next level, all flags have default values assigned, which will be used if none were explicitly assigned.
	In the case of copy, the defaults are $urls and $dir~. So how do we know what the defaults are?
	Pretty much all plugins have defaults that work from the active to the inactive file list, but this is not exact science.
	It is documented in the documentation of the plugin, but the easiest solution is to just check the status line below the command line.
	This will give feedback on how the command will be interpreted at all times and the defaults will be explicit here.
</p>

<code>:new -d $dir `mydir`</code><br/>
<p>Creates a new file in the current directory named "mydir"</p>

<code>:n $dir `mydir`</code><br/>
<p>
	The exact same command, remember? new starts with the unique letter n and the directory flag is the default.
	Now, the first argument is the default argument, but we still have to specify it,
	because if we leave it out, then `mydir` will be assumed to be the first argument, and not the second.
	This can be done smarter, though. If you specify only a dollar '$' symbol, then you explicitly request the default:
</p>

<code>:n $ `mydir`</code><br/>
<p>The exact same command</p>

<code>:n$`mydir`</code><br/>
<p>
	Once again, the exact same command. Whitespace is not significant, remember?
	This one is less readable, but the readable version is still available on the status line.
</p>

<code>:find -ri $dir `*.java`</code><br/>
<p>
	A simple example with multiple flags: find all java files in the active directory.
	Let's apply the tricks we already know:
</p>

<code>:f -ri $ `*.java`</code><br/>
<p>The exact same command, with shorthands.</p>

<code>:f -ir `*.java`</code><br/>
<p>In this case, it's worthwhile to re-order the flags, because we no longer have to request the default for the root flag.</p>

<code>:f -i `*.java`</code><br/>
<p>
	The root flag is a default flag for the find command. Furthermore, the include flag does not override the root flag.
	In fact, the root flag is always added for the find command, so we can simply drop it.
	Default flags are always added AFTER flags requested specifically, so we do not need to supply the directory, but we can.
</p>

<code>:f-i`*.java`</code><br/>
<p>For completeness, the version without whitespace.</p>




<h2>Summary</h2>

As I have shown, Q uses a powerful declarative syntax for specifying commands.
The syntax is quite simple and easy to get used to, although there are some border cases with escape sequences.
Also there are a lot of shorthands, that make it possible to quickly specify all sorts of file operations.
You are not required to use them, but if you actively use the command line, then I suspect that your need for them will grow.
This concludes all the lessons on how the syntax of the command line works.

</div>
<#include "include/tail.tpl"/>
