<%@page pageEncoding="ISO-8859-1" %>
<p>in page: Caf� twee�ntwintig</p>
<p>
  in attribute: <mm:write value="Caf� twee�ntwintig" />
</p>
<p>mmbase:  
		<mm:node number="$node" notfound="skip">
      <mm:field name="subtitle" />
    </mm:node>
</p>
