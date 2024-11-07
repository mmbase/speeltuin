version=1.8
subversion=-1
zip=jquery-ui-$(version).custom.zip
js=jquery-ui-$(version).custom.min.js

.PHONY: clean

jquery.ui.war: $(zip) build/mmbase/jquery/jquery-ui.jspx
	mkdir -p build/mmbase/jquery/css
	mkdir -p build/work
	unzip -o $(zip) js/* -d build/work
	unzip -o $(zip) css/* -d build/work
	cp -a build/work/js/*  build/mmbase/jquery/
	cp -a build/work/css/smoothness/* build/mmbase/jquery/css/
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
	wget  http://jqueryui.com/download/$(zip)



clean:
	rm -rf build
	rm -rf jquery.ui.war
