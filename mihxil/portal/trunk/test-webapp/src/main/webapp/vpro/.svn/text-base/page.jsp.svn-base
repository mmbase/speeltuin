<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page">
  <mm:content
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns:fn="http://java.sun.com/jsp/jstl/functions"
      xmlns:mm-sr="http://www.mmbase.org/tags/mm/searchrelate"
      xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
      xmlns:edit="urn:jsptagdir:/WEB-INF/tags/vpro-wizards"
      xmlns:form="urn:jsptagdir:/WEB-INF/tags/vpro-wizards/form"
      xmlns:util="urn:jsptagdir:/WEB-INF/tags/vpro-wizards/util"
      xmlns:list="urn:jsptagdir:/WEB-INF/tags/vpro-wizards/list"
      expires="0" type="application/xhtml+xml"
      language="client" postprocessor="none">

    <form:wizard >

      <edit:path name="Nieuws Bericht" node="${nodenr}" session="newsitem_${not empty param.secondnews ? 'second' : ''}"/>
      <edit:sessionpath/>

      <form:container nodetype="pages">
        <form:createrelation source="${param.parentnodenr}" referDestination="new" role="${param.relationrole}"/>
        <form:textfield field="title"/>
        <form:datefield field="online" />
        <form:datefield field="offline" />
      </form:container>

      <form:related>
        <form:view nodetype="urls"
                   relatedpage="../urls/related"
                   relationrole="posrel"
                   sortable="true"
                   name="gekoppelde url's"/>

        <form:view nodetype="news"
                   relationrole="sorted"
                   sortable="true"
                   relatedpage="../news/related"
                   openwizard="newsitem.jsp"
                   name="gekoppelde nieuws berichten">
          <jsp:attribute name="display">
            <mm:include page="item-publishedstatus.jsp" >
              <mm:param name="nodenr" value="${_nodenr}" />
            </mm:include>
          </jsp:attribute>
        </form:view>

        <form:view nodetype="images"
                   relationrole="posrel" sortable="true"
                   relatedpage="../images/related"
                   name="gekoppelde plaatjes"/>

        <form:view nodetype="attachments"
                   relationrole="posrel" sortable="true"
                   relatedpage="../attachments/related"
                   name="gekoppelde bijlange"/>

        <form:view nodetype="people"
                   relatedpage="../people/related"
                   name="auteur(s) van dit artikel"/>

      </form:related>
    </form:wizard>
  </mm:content>
</jsp:root>
