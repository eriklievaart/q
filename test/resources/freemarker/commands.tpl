<#include "include/head.tpl"/>
<div id="content" style="width: 600px; float: left;">

<h1>Q Commands</h1>
This page will describe how the command line of the Q file browser works.
To call up the command line use the "View =&gt; Show command line" option from the menu.

<p>
	Nothing is more powerful than the native command line, but GUI's are more easy to navigate.
	When working with other file browsers I find myself constantly switching between terminal and file browser.
	This involves manually copying the location in the process, which is tedious and unnecessary.
	So I decided to make both available at the same time. I believe both have their use.
	In Q native commands are invoked in the active directory.
</p>

<p>
	You can only invoke native commands on the local file system, 
	so it does not work when navigating the in-memory file system.
</p>

<p>
	Before I start, please note that Q has two file lists.
	When I use the word active, I mean the file list that was last selected.
	This is an important concept, because most operations in the application work on the active file list.
	Q grays out the inactive URL bar. The active file list has a black URL bar.
</p>




<h1>Navigation</h1>
<p>
	The first thing you can use the command line for, is for navigating directories.
	Simply enter a URL on the command line.
</p>

<code>file:///tmp</code><br/>

<p>Do not use back slashes '\'. Always use forward slashes '/' in Q.</p>

<p>
	Q currently only supports the file protocol (native file system) and the mem protocol (in-memory file system).
	The application was designed in such a way, that supporting new protocols will be possible in the future.
</p>

<p>
	You can enter a location relative to the root if you start with a '/'.
	The example below has the roughly same meaning as the previous example.
</p>

<code>/tmp</code><br/>

<p>
	It is also possible to open a location relative to the active location.
	Simply enter a relative path:
</p>

<code>program files</code><br/>

<p>
	It's not necessary to type the full name of the folder you want to open.
	Q will open the first partial match if there is one.
</p>

<code>/m</code><br/>
<p>Could open for example "file:///media".</p>
<code>/mn</code><br/>
<p>Could open for example "file:///mnt".</p>
<code>/m/1/m</code><br/>
<p>Could open for example "file:///media/1000GB/music".</p>

<p>
	Sometimes the part that makes a file name unique is at the end and not at the beginning of the file name.
	You can use wildcards in such cases. The following command opens the first folder containing "load": 
</p>

<code>*load</code><br/>
<p>So it could match for example "download" or "jar-loader".</p>

<p>
	The backtick '`' character anywhere in a location, causes problems explained in the plugin section.
	You can solve this using '?' instead, the single character wildcard. 
</p>

<p>
	Partial matches and wildcard match are relatively slow, 
	because they require full directory listings on every location in the path. 
	Usually this is resolved faster than you can type, but can be annoying if the path contains large folders.
	Typing in the location exactly will give the quickest results.  
</p>




<h1>Other Commands</h1>

<p>
	The command line is not only useful for navigation.
	On the command line, the first character is significant, because it determines the receiver of the command.
	I will give some examples to clarify, but don't worry about the specifics, these will be explained in the templates section.
</p>

<code>!ls -l</code><br/>
<p>Invokes the command ls -l on the native command line. Under linux this would return a directory listing.</p>

<code>:location -s</code><br/>
<p>Swaps the locations opened in the left and right file lists. The colon ':' is used to invoke plug ins (bottom part of menu).</p>

<p>
	If you want to open a location starting with one of the prefixes: [-~:$@!?] enter a question mark '?' first.
	Such symbols are rare at the start of filenames, but they do occur on some OS.
</p>

<code>?:file</code><br/>
<p>Opens :file</p>

<code>?_file</code><br/>
<p>Opens _file</p>

<p>The following sections will examine these ways of invoking the engine in greater detail.</p>




<h1>Plug-ins</h1>
<h2>Why plug-ins?</h2>

Plug-ins, also called ShellCommands, 
use a syntax similar to the native command line to invoke operations in the application.
This is nice and all, but ShellCommands are not as powerful as the native command line, 
so why invent a new syntax for something that already exists?

<p>I believe that ShellCommands make a better match for the application for a couple of reasons:</p>

<ul>
	<li>Feedback</li>
	<li>Regularity</li>
	<li>Context</li>
	<li>Queuing (see introduction)</li>
	<li>Extensibility</li>
</ul>

<p>
	The following will examine these arguments in more detail.
	Queuing and extensibility won't have a section. Queuing is explained in the introduction.
	Extensibility was a problem in older versions of Q, but since Q 3.0 the application is built on OSGI.
	There is a very simple contract and adding new plug-ins is just a matter of copying a bundle to the "bundle" directory.
	If you just want to know how to use the command line, skip this section and continue reading on the syntax page.
</p>

<h3>Feedback and Regularity</h3>

<p>
	When I first started using Linux, the command line drove me nuts.
	There is no agreed structure of the commands.
	Command line flags can be upper case, lower case, single dash, double dash, no dash, single letter or words.
</p>

<pre>ps ax
ls -a
ls -R
ls --help
</pre>

<p>
	As an additional factor, the amount of feedback you get is minimal.
	I never understood how other people knew what to invoke?
	You type a command on the command line, and the only way to figure out if it works, is by invoking it.
	If you made a mistake, you will get "command not found" or a syntax problem or whatever.
	Every time I wanted to get something done I needed to turn to google.
	Now don't get me wrong, this is not intended as a rant at Linux.
	The problems the Linux command line has to solve are of a completely different order of magnitude.
	Still, I wanted Q to be easier to use (and the command line to be platform independent).
</p>

<p>
	So how does Q change all this? <br/>
	As soon as you type a colon ':' on the command line a status bar will popup.
	This status line will show all available plug-ins. No need to memorize.
	Similarly, when you type a dash, the status line will show all available flags.
	At any time, if a command is considered syntactically valid, 
	the status line will show how Q interprets the command.
	Q strives to give the maximum amount of feedback possible.
	The status line includes the full name of active flags and their parameters (default or supplied).
</p>

<p>
	Some additional steps were taken to make ShellCommands as easy to use as possible:
	<ul>
		<li>Easy and regular syntax.</li>
		<li>Whitespace is not significant.</li>
		<li>ShellCommands and their flags are not case sensitive.</li>
		<li>Piped content does not require escape sequences.</li>
	</ul>
</p>


<h3>Context</h3>

<p>
	The next way Q makes it easier, is by using context to minimize what you have to type.
	On the native command line there is limited context. The only relevant piece of information is the current directory.
	File browsers with a UI are different, however. 
	In the case of Q there are 2 open directories, and there are always files selected left and right.
	So whereas on the native command line you have to type what you want to move where, Q can fill in the gaps.
	I believe UI's are more suited for browing and selecting files. A context aware command line combines the best of both worlds.
</p>

<p>The syntax of the Q command line can be found under the menu item "syntax"</p>

</div>
<#include "include/tail.tpl"/>