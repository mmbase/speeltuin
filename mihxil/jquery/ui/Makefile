version=1.14.1
subversion=-1
zip=jquery-ui-$(version).custom.zip
js=jquery-ui-$(version).custom.min.js

.PHONY: clean

jquery.ui.war: $(zip) build/mmbase/jquery/jquery-ui.jspx
	mkdir -p build/mmbase/jquery/css
	mkdir -p build/work
	unzip -o $(zip) jquery-ui-${version}.custom/jquery* -d build/work
	unzip -o $(zip) jquery-ui-${version}.custom/images/* -d build/work
	cp -a build/work/jquery-ui-${version}.custom/*  build/mmbase/jquery/
	jar cf $@ -C build mmbase


deploy: jquery.ui.war
	mvn deploy:deploy-file \
	 -DgeneratePom.description='JQuery ui' -DgroupId=org.mmbase.jquery -DartifactId=jquery-ui   \
	 -Dversion=$(version)$(subversion) -Dpackaging=war \
	-Dfile=jquery.ui.war \
	-Durl=https://oss.sonatype.org/service/local/repositories/releases/content -DrepositoryId=ossrh

build/mmbase/jquery/jquery-ui.jspx:
	mkdir -p build/mmbase/jquery/
	sed s/JQUERYVERSION/$(version)/g mmbase/jquery/jquery-ui.jspx |\
	sed s/JQUERYJS/$(js)/g > $@

$(zip):
	curl -o $@ 'https://download.jqueryui.com/download' \
  --data-raw 'version=${version}&widget=on&position=on&data=on&disable-selection=on&focusable=on&form-reset-mixin=on&jquery-patch=on&keycode=on&labels=on&scroll-parent=on&tabbable=on&unique-id=on&widgets%2Fdraggable=on&widgets%2Fdroppable=on&widgets%2Fresizable=on&widgets%2Fselectable=on&widgets%2Fsortable=on&widgets%2Faccordion=on&widgets%2Fautocomplete=on&widgets%2Fbutton=on&widgets%2Fcheckboxradio=on&widgets%2Fcontrolgroup=on&widgets%2Fdatepicker=on&widgets%2Fdialog=on&widgets%2Fmenu=on&widgets%2Fmouse=on&widgets%2Fprogressbar=on&widgets%2Fselectmenu=on&widgets%2Fslider=on&widgets%2Fspinner=on&widgets%2Ftabs=on&widgets%2Ftooltip=on&effect=on&effects%2Feffect-blind=on&effects%2Feffect-bounce=on&effects%2Feffect-clip=on&effects%2Feffect-drop=on&effects%2Feffect-explode=on&effects%2Feffect-fade=on&effects%2Feffect-fold=on&effects%2Feffect-highlight=on&effects%2Feffect-puff=on&effects%2Feffect-pulsate=on&effects%2Feffect-scale=on&effects%2Feffect-shake=on&effects%2Feffect-size=on&effects%2Feffect-slide=on&effects%2Feffect-transfer=on&theme=ffDefault%3DArial%252CHelvetica%252Csans-serif%26fsDefault%3D1em%26fwDefault%3Dnormal%26cornerRadius%3D3px%26bgColorHeader%3De9e9e9%26bgTextureHeader%3Dflat%26borderColorHeader%3Ddddddd%26fcHeader%3D333333%26iconColorHeader%3D444444%26bgColorContent%3Dffffff%26bgTextureContent%3Dflat%26borderColorContent%3Ddddddd%26fcContent%3D333333%26iconColorContent%3D444444%26bgColorDefault%3Df6f6f6%26bgTextureDefault%3Dflat%26borderColorDefault%3Dc5c5c5%26fcDefault%3D454545%26iconColorDefault%3D777777%26bgColorHover%3Dededed%26bgTextureHover%3Dflat%26borderColorHover%3Dcccccc%26fcHover%3D2b2b2b%26iconColorHover%3D555555%26bgColorActive%3D007fff%26bgTextureActive%3Dflat%26borderColorActive%3D003eff%26fcActive%3Dffffff%26iconColorActive%3Dffffff%26bgColorHighlight%3Dfffa90%26bgTextureHighlight%3Dflat%26borderColorHighlight%3Ddad55e%26fcHighlight%3D777620%26iconColorHighlight%3D777620%26bgColorError%3Dfddfdf%26bgTextureError%3Dflat%26borderColorError%3Df1a899%26fcError%3D5f3f3f%26iconColorError%3Dcc0000%26bgColorOverlay%3Daaaaaa%26bgTextureOverlay%3Dflat%26bgImgOpacityOverlay%3D0%26opacityOverlay%3D30%26bgColorShadow%3D666666%26bgTextureShadow%3Dflat%26bgImgOpacityShadow%3D0%26opacityShadow%3D30%26thicknessShadow%3D5px%26offsetTopShadow%3D0px%26offsetLeftShadow%3D0px%26cornerRadiusShadow%3D8px&scope='



clean:
	rm -rf build
	rm -rf jquery.ui.war
