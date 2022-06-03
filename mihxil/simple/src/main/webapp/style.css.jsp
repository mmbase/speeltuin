/* -*- mode: css -*- */
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib tagdir="/WEB-INF/tags" prefix="simple"
%><%@page session="false" %><mm:content  expires="0" type="text/css">
<mm:cloud>
<mm:import id="width" vartype="integer">1000</mm:import>
<mm:import id="menuitem_height" vartype="integer">100</mm:import>
<mm:import id="menuitem_margintop" vartype="integer">20</mm:import>
<mm:import id="intro_width" vartype="integer">200</mm:import>
<mm:import id="margin_left" vartype="integer">100</mm:import>
<mm:import id="menumargin" vartype="integer">20</mm:import>
<mm:import id="menuitem_margin" vartype="integer">20</mm:import>
<mm:import id="menuitem_width" vartype="integer">${(width - intro_width - menumargin * 2  - menuitem_margin * 3) / 4}</mm:import>
<mm:import id="radius" vartype="integer">20</mm:import>
<mm:node number="${initParam.startnode}">


html {
  background-color: #ffa500;
  color: #333;
  font-family: Arial;
  margin: 0;
  padding: 0;
  <simple:image role="background">
    background-image: url(<mm:image template="s(300)+colorspace(Gray)+fill(rgba(255, 165, 0, 0.5))+draw(rectangle 0,0,1000,1000)" />);
  </simple:image>
  background-repeat: no-repeat;
  background-position: left center;
}
body {
  margin-top: 8px;
  margin-left: 2px;
  margin-right: 2px;
  margin-bottom: 0;
}

.container {
  top: 0px;
  left: 0px;
  position: relative;
  display: block;
  max-width: ${width}px;
  min-width: ${intro_width}x;
  margin-left: auto;
  margin-right: auto;
}


div.content,
div#menu {
  position: absolute;
  left: ${intro_width}px;
  background-color: #fff;
}
div.content {
  background-color: white;
  overflow-y: auto;
  overflow-x: hidden;
  top: ${menuitem_height}px;
  /*
  -moz-border-radius-bottomleft: ${radius}px;
  -webkit-border-bottom-left-radius: ${radius}px;
  border-bottom-left-radius: ${radius}px;
  -moz-border-radius-bottomright: ${radius}px;
  -webkit-border-bottom-right-radius: ${radius}px;
  border-bottom-right-radius: ${radius}px;
  */
}

div.content h3 {
  margin-top: 60px;
}
div#menu {
  font-family: Verdana;
  top: 0px;
  height: ${menuitem_height}px;
  /*
  -moz-border-radius-topleft: ${radius}px;
  -webkit-border-top-left-radius: ${radius}px;
  border-top-left-radius: ${radius}px;
  -moz-border-radius-topright: ${radius}px;
  -webkit-border-top-right-radius: ${radius}px;
  border-top-right-radius: ${radius}px;
  */
}


div.content > *,
div.content span.inner > * { /* from roundedCorner */
  padding-left: ${margin_left}px;
  padding-right: 1ex;
}
div.content  img.image-left {
  margin-left: -${margin_left - menumargin}px;
  margin-right: 10px;
}
div.content h2 {
  display: none;
}

div#menu ul {
  margin-left: ${menumargin}px;
  display: block;
  padding: 0;
  margin-top: ${menuitem_margintop}px;
  margin-bottom: 0px;
}
div#menu ul li {
  display: block;
  float: left;
  margin-right: ${menuitem_margin}px;
  background-color: #aaa;
  text-align: center;
  cursor: pointer;
  width: ${menuitem_width}px;
  height: ${menuitem_height - menuitem_margintop}px;
}
div#menu ul li.last {
  margin-right: 0px;
}

div.intro .head {
  font-family: Verdana;
  margin-top: ${menuitem_margintop}px;
  height: ${menuitem_height - menuitem_margintop}px;
  width: ${intro_width - 40}px;
  color: white;
  text-align: center;

}
div.intro > *,
div.intro span.inner > * { /* from roundedCorner */
  margin-left: 20px;
  margin-right: 20px;
}

<simple:image role="teaser">

div.intro h1 {
  display: block;
  left: 0px;
  margin: 0;
  padding: 0;
  position: relative;
  width: ${intro_width - 40}px;
  height: 30px;
  top: ${menuitem_height - menuitem_margintop - 30}px;
  color: #000;
  font-weight: bold;
  font-size: 13pt;
  text-transform: uppercase;
}

     div.intro .head {
      background-image: url(<mm:image  template="f(png)+s(${intro_width - 40})+gravity(Center)+crop(${intro_width - 40}x${menuitem_height - menuitem_margintop}+0+0)+fill(rgba(255,255,255,0.6))+draw(rectangle 0,${menuitem_height - menuitem_margintop - 30},${intro_width-40},${menuitem_height - menuitem_margintop})" />);
     }
</simple:image>

<mm:import id="menutemplate">f(png)+s(${menuitem_width})+gravity(Center)+crop(${menuitem_width}x${menuitem_height - menuitem_margintop}+0+0)</mm:import>
<mm:import id="menutemplate2">fill(rgba(255,255,255,0.6))+draw(rectangle 0,${menuitem_height - menuitem_margintop - 30},${menuitem_width},${menuitem_height - menuitem_margintop})</mm:import>
<mm:relatednodescontainer role="index" type="segments">
<mm:sortorder field="index.pos" />
<mm:relatednodes id="segment">
   <simple:image role="teaser">

   /* 'preload' the hovers */
   li#menu${segment} {
      background-image: url(<mm:image  template="${menutemplate}+${menutemplate2}" />);
   }

   li#menu${segment}.inactive {
      background-image: url(<mm:image  template="${menutemplate}+blur(10x3)+${menutemplate2}"/>);
   }
   li#menu${segment}.active,
   li#menu${segment}.hover {
      background-image: url(<mm:image  template="${menutemplate}+${menutemplate2}" />);
   }
   </simple:image>
   </mm:relatednodes>
   </mm:relatednodescontainer>

div#menu a {
  text-transform: uppercase;
  width: ${menuitem_width}px;
  display: block;
  position: relative;
  text-decoration: none;
  height: 30px;
  left: 0px;
  top: ${menuitem_height - menuitem_margintop - 30}px;
  color: #666;
  font-weight: bold;
}

div#menu li.active a,
div#menu li a:hover {
  color: #000;
}

div.intro {
  padding: 0px;
  margin: 0;
  position: absolute;
  left: -2px;
  width: ${intro_width - 2}px;
  background-color: #fff;
  /*
  -moz-border-radius: ${radius}px;
  -webkit-border-radius: ${radius}px;
  border-radius: ${radius}px;
  */
}
div.footer {
  border-top: solid 1px white;
  position: absolute;
  top: 900px;
  height: 20px;
  background-color: #ffc722;
  text-align: center;
}
div.footer p {
  margin: 0;
  padding: 0;
}

a.mm_portal_edit {
  position: relative;
}
div.icons {
  text-align: center;
}
div.icons img {
  border: none;
  margin-right: 1ex;
}
</mm:node>
</mm:cloud>
</mm:content>
