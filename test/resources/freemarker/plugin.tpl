<#include "include/head.tpl"/>

<div id="content" style="width: 1000px; float: left;">
	
	<h1>Documentation of the "${command}" plugin</h1>
	${doc}
	
	<h3>Default command</h3>
	<p><code>${default}</code></p>
	<br/>
	
	<h3>Flags</h3> 	
	<#list metadata.characterFlags as flag>
		<#assign name = metadata.getFlagMetadata(flag).name> 
		<p>
			<span class="flag">-${flag}</span> (${name})
			<br/>
			<div style="position: relative; left: 140px">
				${flagDoc[name]}
				<p><code>${examples[name]}</code></p>
				<br/>
			</div>
		</p>
	</#list>
	
	<#if piped??>
		<h3>Piped contents</h3>
		${piped}
	</#if>
	
	<#if description??>
		<h3>Description</h3>
		${description}
	</#if>
	
</div>

<#include "include/tail.tpl"/>