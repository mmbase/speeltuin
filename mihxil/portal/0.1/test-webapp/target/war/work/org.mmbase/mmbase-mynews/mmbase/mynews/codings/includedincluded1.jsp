<%@page session="false"
  language="java"  
  pageEncoding="ISO-8859-1"
  contentType="text/html;charset=ISO-8859-1" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>
<p>included (iso-1): Caf� twee�ntwintig. <mm:write value="Caf� twee�ntwintig" />
<mm:import externid="node" from="parameters">codings</mm:import> 
<mm:cloud>
  subtitle of node '<mm:write referid="node" />': 
  <mm:node number="$node" notfound="skip">
    <mm:field name="subtitle" />
  </mm:node>
</mm:cloud>
</p>
