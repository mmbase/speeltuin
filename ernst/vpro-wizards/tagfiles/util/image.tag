<%@ tag body-content="empty"  %>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"%>

<%@ attribute name="template" %>
<%@ attribute name="nodenr" required="true" %>
<%@ attribute name="urlvar" required="true" rtexprvalue="false"%>

<%@ variable name-from-attribute="urlvar" alias="url" scope="AT_END"%>

<%--
    this tag will create the url to an mmbase image, and set it in the page context after it's done
    something should be created to make this nice and configurable
--%>

<mm:cloud >
    <mm:node number="${nodenr}" notfound="skipbody">
        <%--
        <c:set var="url">http://images.vpro.nl/images/${nodenr}${template}</c:set>
        --%>
        <c:set var="url"><mm:image template="${template}"/></c:set>
    </mm:node>
</mm:cloud>
<c:if test="${empty url}">
    <util:throw message="util:image tag: node for image not found"/>
</c:if>

