<#include "include/head.tpl"/>

<div id="content" style="width: 600px; float: left;">

<h1>Introduction</h1>
The Q file browser is a multi-protocol file browser targeted at advanced computer users.
Q has few confirmation dialogs and simply overwrites (hidden and read only) directories and files without confirmation. 
For normal file operations (e.g. copy, move, delete), the GUI should be intuitive enough to use.
The GUI of Q offers few features that will make people want to switch. 
The real strength of the application is in the command line.
In this chapter I will discuss unusual GUI features only. 
These are the features I find myself missing in other file browsers.




<h2>Calculate Directory Sizes</h2>
A feature I often miss in file browsers is calculating directory sizes.
<p>
Most file browsers are capable of calculating the size of a directory, but only do this one by one.
In Q you can take a snapshot of (sub) directory sizes with a single invocation.
If the contents of these directories change, the sizes won't be updated until you recalculate again.
For browsing sizes in more detail, I suggest a tool like file light.
</p>




<h2>Queuing</h2>
Normally on file browsers when you start a copy, move or delete operation, it is run as a separate process.
If you have a couple these running, your computer slows down considerably.
This is caused by the head of the drive switching back and forth between files (except for SSD's).
I chose a different approach; all file operations are queued and invoked one by one (thus the name Q).
I find this ideal for normal use cases of a file browser. 




<h2>Warping</h2>
I spend most of my time in a short list of directories.
In traditional file browsers, I have to regularly navigate the full path to these directories.
Which entails clicking through the same directories over and over again.
Q keeps an index of recently visited directories and you can warp between them, simply by entering the name.
I keep the names of my directories unique as to prevent confusion which directory is intended.
If you know (suspect) part of the name is unique, then you only need the unique part, not the full name.




<h2>Command Line</h2>
The command line has a central role in Q.
As you are browsing the file system, you can invoke the native command line at the opened location at any time, 
without having to open a separate terminal.

<p>
	Q also offers a custom syntax for interacting with the core of the application.
	The rest of the documentation of the application covers the command line and scripting.
	Understanding the command line will make it possible to perform some of the more unusual file operations quickly.
	Examples: moving files with only specified file extensions or finding file paths matching a regular expression.
</p>

</div>
<#include "include/tail.tpl"/>