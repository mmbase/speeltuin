<%@page contentType="text/html;charset=UTF8" %><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards/"
%><%@ taglib prefix="tags" tagdir="/WEB-INF/tags"
%><%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form"
%><%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list"
%><%@ taglib prefix="mm-portalt" tagdir="/WEB-INF/tags/mm/p"
%><%@ taglib prefix="related" tagdir="/WEB-INF/tags/vpro-wizards/related"
%><%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib prefix="mm-portal" uri="http://www.mmbase.org/tags/mm/portal"
%><mm:content expires="0" language="nl">
<mm:cloud>
  <form:wizard>


      <jsp:attribute name="header" >
        <style>
          tr.page:hover td.click {
             cursor: pointer;
          }
          tr.current.offline {
            background-color: #ffa;
          }
          tr.current.online {
            background-color: #aff;
          }
          tr.online {
            background-color: #afa;
          }
          tr.offline {
            background-color: #faa;
          }
          form {
            display: inline;
          }
        </style>
        <script type="text/javascript">
          $(function() {
          $("tr.page td.click").click(function() {
          var href = $(this).closest("tr").find("td a").attr("href");
          document.location = href;
          });
          });
          </script>
      </jsp:attribute>
      <jsp:body>
        <mm:cloud method="asis">
          <mm:import externid="clone" />
          <mm:present referid="clone">
            <mm:node number="${clone}">
              <mm:nodefunction id="newnode" name="clone" />
            </mm:node>
            <p>Created node ${newnode}</p>
          </mm:present>
          <mm:import externid="delete" />
          <mm:present referid="delete">
            <mm:node number="${delete}">
              <mm:deletenode deleterelations="true" />
            </mm:node>
            <p>Deleted node ${delete}</p>
          </mm:present>


          <edit:path node="${_node}"  session="sectie_pageoverview" reset="true" />
          <edit:sessionpath />
          <mm:import externid="showdate">tomorrow</mm:import>
          <select id="showdate" name="showdate">
            <mm:option value="now" compare="${showdate}">Nu</mm:option>
            <mm:option value="tohour+1day" compare="${showdate}">Morgen</mm:option>
            <mm:option value="tohour+1week" compare="${showdate}">Volgende week</mm:option>
            <mm:option value="nextsaturday+7hour" compare="${showdate}">Zaterdag</mm:option>
            <mm-portal:pages online="false">
              <mm:context>
                <c:if test="${_node.online ne online}">
                  <mm:field name="online" id="online">
                    <mm:time  offset="${10 * 60}" format="yyyy-MM-dd'T'HH:mm">
                      <mm:option value="${_}" compare="${showdate}">
                        <mm:time  writer="online" offset="${10 * 60}" format=":LONG.LONG" />
                      </mm:option>
                    </mm:time>
                  </mm:field>
                </c:if>
              </mm:context>
            </mm-portal:pages>
            <mm:option value="now-1year" compare="${showdate}">Vorig jaar</mm:option>
          </select>

          <mm:link page="/">
            <mm:param name="edit">true</mm:param>
            <input type="submit" value="Herlaad pagina alsof het deze datum is"
                   onclick="window.parent.location = '${_}' + '&amp;showdate=' +  encodeURIComponent($('select#showdate').val()); return false;" />
          </mm:link>
          <c:if test="${showdate ne 'now'}">
            <p>Nu: <mm:time time="${showdate}" format=":FULL.FULL" /></p>
          </c:if>
          <mm-portalt:pages online="false">
          </mm-portalt:pages>

      </mm:cloud>
    </jsp:body>
  </form:wizard>
</mm:cloud>
</mm:content>
