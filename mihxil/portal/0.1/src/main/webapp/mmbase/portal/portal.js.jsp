// -*- mode: javascript; -*-
<%@page contentType="text/javascript; charset=UTF-8" session="false"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><mm:content type="text/javascript" language="${param.language}">
<fmt:bundle basename="org.mmbase.portal.portal">
$.ui.dialog.defaults.bgiframe = true;
function MMBasePortal(root) {
    this.editordir = $("head meta[name='org.mmbase.portal.editordir']").attr("content");
    if (root == null) root = document;
    this.root = root;
    var self = this;
    this.edits = 0;
    this.parameters = {};
    this.options = {
        autoOpen: false,
        modal: true,
        height: 0.8 * $(window).height(),
        width: 0.9 * $(window).width(),
        close: function() {
            $("#mm_portal_edit").empty();
            $("body").trigger("mmpClosed", [self]);
        }
    }
    $(document).ready(function() {
        self.init();
      });
}

MMBasePortal.prototype.init = function() {
    var self = this;
    if ($("#mm_portal_edit").length == 0) {
        $("body").append("<div id='mm_portal_edit' title='<fmt:message key="edit" />'></div>");
        $("#mm_portal_edit").dialog(self.options);
    }

    this.addLinksToEditables();
    this.addLinkToBody();
}


MMBasePortal.prototype.addLinkToElement = function(el) {
    var self = this;
    var div = el;
    var classes = $(div).attr("class").split(' ');
    var editor = "";
    var params = {};
    for (var i in classes) {
        var cl = classes[i];
        if (cl.indexOf("mm_ea_") == 0) {
            var a = cl.substring("mm_ea_".length);
            var u = a.indexOf("_");
            params[a.substring(0, u)] = a.substring(u + 1);
        }
        if (cl.indexOf("mm_editor_") == 0) {
            editor = cl.substring("mm_editor_".length);
        }
    }
    var args = "";
    for (var a in params) {
        if (args.length == 0) {
            args += "?";
        } else {
            args += "&";
        }
        args += a + "=" + params[a];
    }
    var a = $("<a class='mm_portal_edit'><fmt:message key="edit" /></a>");
    $(a).attr("href", self.editordir + editor + "/" + args);

    $(div).append(a);
    $(a).click(function() {
            try {
                var index = this.href.indexOf('?');
                var q = this.href.substring(index);
                self.parameters = self.parseQueryString(q);
                var ift = "<iframe class='mm_portal_iframe' src='" + this.href + "'> </iframe>";
                var iframe = $(ift);
                $("#mm_portal_edit").append(iframe);
                $("body").trigger("mmpOpen", [self]);
                $("#mm_portal_edit").dialog("open");
                self.edits++;
            } catch (e) {
                console.log(e);
            }
            return false;
        });
}


MMBasePortal.prototype.addLinksToEditables = function() {
    var self = this;
    $(this.root).find(".editable").each(function() {
            self.addLinkToElement(this);
        });
}

MMBasePortal.prototype.addLinkToBody = function() {
    var body = $("body")[0];
    this.addLinkToElement(body);
}


MMBasePortal.prototype.parseQueryString = function (location) {
    if (location.indexOf("?") == 0) location = location.substring(1);
    var result = {};
    var split = location.split("&");
    for (var i = 0; i < split.length; i++) {
        var entry = split[i].split("=");
        result[entry[0]] = entry[1];
    }
    return result;
}

MMBasePortal.prototype.joinQuery = function(query) {
    var result = "";
    for (var k in query) {
        if (result.length == 0) {
            result += "?";
        } else {
            result += "&";
        }
        result += k + "=" + query[k];
    }
    return result;
}

</fmt:bundle>
</mm:content>
