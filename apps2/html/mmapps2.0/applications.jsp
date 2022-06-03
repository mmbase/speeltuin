<!-- This is the main page, it gives an overview of all deployed applications
 	add of all commands. -->
<%@page import="org.mmbase.module.core.*" %>
<%@page import="org.mmbase.application.*" %>
<%@page import="java.util.*" %>

<HTML>
<HEAD>
<TITLE>
	Applications 2.0 - All Applications
</TITLE>
</HEAD>
<BODY TEXT="#42BDAD" BGCOLOR="#00425a" LINK="#FFFFFF" ALINK="#FFFFFF" VLINK="#FFFFFF">
<BR>

<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=3>
<LI><A HREF="klad/addApplication.jsp">add Application</A><BR>
<LI><A HREF="klad/providerinfo.jsp">Own provider settings</A><BR>
<LI><A HREF="klad/manageProviders.jsp">Manage remote providers</A><BR>
<LI><A HREF="edit/editApplicationInfo.jsp?appname=">create Application</A><BR><BR>



<TABLE width=600 cellspacing=1 cellpadding=3 border=0>

<TR>
	<TD BGCOLOR="42BDAD" WIDTH="600" COLSPAN="5">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=3>
	<B>Deployed Applications</B>
	</TD>
</TR>
	<!-- shows the components in the overview of a certain application -->




<!-- shows the header of the components listed in application.shtml -->
<TR>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Edit</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Version</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Auto Deploy</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Description</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Tasks</B></CENTER>
	</TD>
</TR>

<% ApplicationManager am=MMBase.getApplicationManager();
   Hashtable list=am.getApplications();
   for (Enumeration e = list.elements();e.hasMoreElements();) {
      Application app = (Application)e.nextElement();
      int ipv=app.getInstalledVersion();
      String version="not installed";
      if (ipv!=-1) {
      	 version=""+ipv;
      }
%>
<TR>
	<TD BGCOLOR="#000000">
	<FONT COLOR="#FFFFFF" FACE=helvetica,arial,geneva SIZE=3>&nbsp;
    	<A HREF="edit/editComponents.jsp?appname=<%=app.getName()%>"><%=app.getName()%></A>
	</TD>
	<TD BGCOLOR="#000000">
	<FONT COLOR="#FFFFFF" FACE=helvetica,arial,geneva SIZE=2>&nbsp;
        <CENTER><%=version%></CENTER>
	</TD>
	<TD BGCOLOR="#000000">
	<FONT COLOR="#FFFFFF" FACE=helvetica,arial,geneva SIZE=2>&nbsp;
        <CENTER><%=app.getAutoDeploy()%></CENTER>
	</TD>
	<TD BGCOLOR="#000000">
	<FONT COLOR="#FFFFFF" FACE=helvetica,arial,geneva SIZE=2>&nbsp;
        <CENTER><%=app.getDescription()%></CENTER>
	</TD>
	<TD BGCOLOR="#000000">
	<FONT COLOR="#FFFFFF" FACE=helvetica,arial,geneva SIZE=2>&nbsp;
	<CENTER>none</CENTER>	
	</TD>
</TR>
<% } %>
</TABLE>
</BODY>
</HTML>
