<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<link href="style.css" rel="stylesheet" media="screen" type="text/css"/>
</head>
<body>

<div id="index" style="width: 140px; float: left;">
	<h1>Menu</h1>
	<a href="introduction.xhtml">introduction</a>
	<a href="commands.xhtml">commands</a>
	<a href="syntax.xhtml">syntax</a>
	<a href="templates.xhtml">templates</a>
	
	<h3>Plugins</h3>
	<#list plugins as plugin>
		<a href="${plugin}.xhtml">${plugin}</a>
	</#list>
</div>
