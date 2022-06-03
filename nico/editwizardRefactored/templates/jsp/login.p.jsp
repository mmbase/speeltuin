<%--
 ************* COPIED FROM /mmbase/edit/basic/login.p.jsp *********************************
   TODO NEED GENERAL PLACE
--%>
<%@ page import="org.mmbase.security.AuthenticationData,org.mmbase.bridge.*,org.mmbase.util.functions.*,java.util.*" 
%><%@  taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><%!
   String getPrompt(String key, Locale locale) {
     try {
       return ResourceBundle.getBundle(AuthenticationData.STRINGS, locale).getString(key);
     } catch (MissingResourceException mre) {
       return key;
     }
   }
%>
<mm:import from="request" externid="language">en</mm:import>
<mm:import from="request" externid="country"></mm:import>
<mm:import from="request" externid="sessionname">cloud_mmbase</mm:import>

<mm:content type="text/html" language="$language" country="$country" expires="0" jspvar="locale" escaper="none">



<mm:import from="request" externid="reason">please</mm:import>
  <mm:import from="request" externid="exactreason" />
  <mm:import from="request" externid="usernames" />
  <mm:import from="request" externid="referrer">.</mm:import>
  <mm:compare referid="reason" value="failed">
    <p class="failed">
      <%=getPrompt("failed", locale)%> <mm:write referid="exactreason"><mm:isnotempty>(<mm:write />)</mm:isnotempty></mm:write>.
    </p>
  </mm:compare>
  <mm:compare referid="reason" value="rank">
    <p class="failed">
      <%=getPrompt("failed_rank", locale)%>
    </p>
  </mm:compare>
  <table>
    <%
      AuthenticationData authentication = ContextProvider.getDefaultCloudContext().getAuthentication();
      String[] authenticationTypes = authentication.getTypes(authentication.getDefaultMethod(request.getProtocol()));
    %>
    <mm:import externid="authenticate" jspvar="currentType" vartype="string" ><%=authenticationTypes[0]%></mm:import>
    <form method="post" action="<mm:write referid="referrer" />" >
    <% Parameter[] params = authentication.createParameters(currentType).getDefinition();
       for (int j = 0; j < params.length ; j++) {
         Parameter param = params[j];
         Class type = param.getType();
         if (type.isAssignableFrom(String.class) && param.isRequired()) {         
    %>       
    <tr>
      <td><%=param.getDescription(locale)%>:</td>
      <td>
        <%-- hack for password fields Would need some method using DataType --%>
        <input type="<%= param.getName().equals("password") ? "password" : "text" %>" name="<%=param.getName()%>">
       </td>
     </tr>
     <%  }
        }
     %>
     <input type="hidden" name="usernames" values="<mm:write referid="usernames" />" />
    <tr><td /><td><input type="hidden" name="command" value="login" /><input type="submit" name="__submit" value="<%=getPrompt("login", locale)%>" /></td></tr>
  </form>
    <tr>
      <td><%=getPrompt("authenticate", locale)%>:</td>
      <td>
        <form method="post" name="auth">
        <select name="authenticate" onChange="document.forms['auth'].submit();">
          <% for (int i = 0 ; i < authenticationTypes.length; i++) { %>
          <option value="<%=authenticationTypes[i]%>" <%= currentType.equals(authenticationTypes[i])? " selected='selected'" : ""%>><%=authenticationTypes[i]%></option>
          <% } %>
          <input type="hidden" name="referrer" value="<mm:write referid="referrer" />" />
          <input type="hidden" name="usernames" values="<mm:write referid="usernames" />" />
         </select>
       </form>
    </tr>
</table>
</mm:content>