/* -*- css -*- */
<%@page session="false" %><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content type="text/css" expires="0">

<mm:import externid="offsetx" vartype="integer">0</mm:import>
<mm:import externid="offsety" vartype="integer">0</mm:import>
<mm:import externid="height" vartype="integer">250</mm:import>
<mm:import externid="width" vartype="integer">300</mm:import>
<mm:import externid="cols" vartype="integer">4</mm:import>
<mm:import externid="rows" vartype="integer">3</mm:import>

.mm_portal_content {
  position: relative;
  width: ${width * cols}px;
  height: ${height * rows}px;
  margin-left: auto;
  margin-right: auto;
}

.mm_portal_content
.block {
  position: absolute;
  border: none;
  overflow: hidden;
  margin: 0 0 0 0;
}

<c:forEach begin="1" end="${cols}" var="i">
.mm_portal_content
.block.width${i} {
  width: ${width * i}px;
}
</c:forEach>

<c:forEach begin="1" end="${rows}" var="i">
.mm_portal_content
.block.height${i} {
  height: ${height * i}px;
}
</c:forEach>

<c:forEach begin="0" end="${cols - 1}" var="i">
.mm_portal_content
.block.x${i} {
  left: ${offsetx + width * i}px;
}
</c:forEach>

<c:forEach begin="0" end="${rows - 1}" var="i">
.mm_portal_content
.block.y${i} {
  top: ${offsety + height * i}px;
}
</c:forEach>

a.mm_portal_edit {
  position: absolute;
  background: yellow;
  right: 0px;
  margin-right: 10px;
  top: 0px;
  z-index: 2000;
}

a.mm_portal_edit {
  position: absolute;
  background: yellow;
  right: 0px;
  margin-right: 10px;
  top: 0px;
  z-index: 2000;
}


.ui-dialog {
  z-index: 2010 !important;
}
.ui-widget-overlay {
  z-index: 2001 !important;
}


iframe.mm_portal_iframe {
  width: 100%;
  height: 100%;
  border: none;
}
</mm:content>
