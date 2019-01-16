<#include "include/head.tpl"/>

<div id="content" style="width: 600px; float: left;">

<h1>Engine Templates</h1>
This section describes the purpose of engine templates.
It is assumed, that you have a basic understanding of the Q shell.

<h2>Why Engine Templates?</h2>
Every command you type on the command line is translated by an engine template.
The first character is used to determine the recipient of the command.
This is the trick that makes it possible to invoke the native command line starting with '!',
invoke plugins using ':' and navigate the file system on letters.
These templates were customizable in Q version 2.0, but currently the feature has been removed due to lack of use.

<p>
	For your understanding, it is important to realize that after the engine template has been applied,
	the result always invokes a plug in.
	So, the '!' template will invoke the "execute" plug in,
	browsing the file system will invoke the "location" plug in
	and so on.
</p>

<p>You can see this substitution taking place if you watch the status line as you type a command.</p>

These engine templates only do basic text replacement.
If using an escape character makes the templated command invalid, then the input does not work.
For instance, you might see a parser error on the status line when entering a location with a backtick [`] in it.




<h2>Available templates</h2>

<h3>invoke plugin</h3>
<p>
This is the simplest template, it works like command mode in vim.
Any line started with a colon ':' is passed to the parser exactly as typed.
</p>
<code>:location -s</code><br/>

<h3>execute in shell</h3>
<p>
When you start a line with '!', the rest of the line is passed to the native shell.
The output of the command is displayed in a new view.
</p>
<code>!ls -la</code><br/>

<p>If you do not want to see the output of the command, use a '-' instead.</p>
<code>-touch file</code><br/>

<p>To repeat a command for every file selected in the active browser use '@'.</p>
<code>@echo $url</code><br/>

<h3>browsing</h3>
<p>Use '~' to quickly jump to a recently visited directory.</p>
<code>~desktop</code><br/>

<p>If you start with a '$', the location will be interpreted as a variable.
To open the parent of the current directory:</p>
<code>$parent</code><br/>

<p>Any other letter will attempt to open the location entered in the active browser.</p>
<code>/tmp</code><br/>

<p>
The underscore '_' is a special template that can be used to find files starting with a command prefix.
Everything after the underscore is interpreted as a literal location,
so this can be used if the name starts with one of the prefixes mentioned above.
</p>
<code>_$mydir</code><br/>
The example above would open the '$mydir' directory, rather than looking for a variable name $mydir.

</div>

<#include "include/tail.tpl"/>
