<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards/"
%><%@ taglib prefix="tags" tagdir="/WEB-INF/tags"
%><%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form"
%><%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list"
%><%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related"
%><%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib prefix="mm-portal" uri="http://www.mmbase.org/tags/mm/portal"
%><mm:content expires="0" language="nl">
<mm:import externid="page" required="true" />
<mm:import externid="portal" required="true" />
<mm:import externid="showdate"  />
<form:wizard nodenr="${page}" title="Pagina"  >
  <mm:node id="page" referid="page" />

  <edit:path node="${page}"  session="sectie_page" />
  <edit:sessionpath/>

  <tags:security cloud="${requestScope.requestcloud}" redirect="/redactie2/geentoegang.jsp"/>


  <form:container nodetype="pages">
    <form:showfield field="number"/>
    <form:textfield field="title"/>
    <form:datefield field="online"/>
    <form:datefield field="offline"/>
    <%-- doesn't work
    <form:input field="show"/>
    --%>
  </form:container>

  <mm:node number="${page}">
    <table>
      <caption>Lay-out</caption>
      <tr><th>x</th><th>y</th><th>size</th><th colspan="2">content</th></tr>
      <mm-portal:blocks>
        <tr>
          <td>${blockposrel.x}</td><td>${blockposrel.y}</td><td>${blockposrel.width}x${blockposrel.height}</td>
          <mm:node referid="block">
            <td><mm:field name="name" /></td>
            <td><mm:field name="description" /></td>
          </mm:node>
        </tr>
      </mm-portal:blocks>
    </table>
  </mm:node>
  <mm:link page="." referids="portal,showdate?">
    <a href="${_}">Terug</a>
  </mm:link>

</form:wizard>
</mm:content>
