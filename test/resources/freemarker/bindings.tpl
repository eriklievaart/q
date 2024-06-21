<#include "include/head.tpl"/>

<div id="content" style="width: 600px; float: left;">

<h1>key bindings</h1>

The default key bindings of Q cannot be changed,
but it is possible to register your own bindings on top of them.

<p>
The following file can be used to create your own bindings:
</p>

<code>~/.config/q/bindings.ini</code><br/>

<p>
The default binding for jump to root is "ctrl alt /".
Here is an example that binds jump to root to "ctrl alt R" in addition to the default binding:
</p>

<p>
<pre>component[q.left.browser.list]
    binding
        keyPressed=control alt R
        action=q.active.root

component[q.right.browser.list]
    binding
        keyPressed=control alt R
        action=q.active.root
</pre>
</p>

<p>
Currently the only way to view the list of built-in actions is by examining the source code.
Apart from built-in actions, custom actions can be defined in the following file:
</p>

<p><code>~/.config/q/actions.properties</code></p><br/>

<p>
This is a simple property file.
The key is the name of the action, the value is the command as it would be entered on the Q command line.
</p>

<p>Here is an example that runs the ls command in the active directory:</p>

<p><code>ls=:execute -ef | ls -l</code></p><br/>

<p>
The action name is prefixed with "user." to avoid collisions.
Here is how you would bind the action defined above to "ctrl alt L":
</p>

<p>
<pre>component[q.left.browser.list]
    binding
        keyPressed=control alt L
        action=user.ls

component[q.right.browser.list]
    binding
        keyPressed=control alt L
        action=user.ls
</pre>
</p>
</div>
<#include "include/tail.tpl"/>
