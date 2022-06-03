versionid=6
version=1.0-beta1
zip=ajaxfileupload1.0.zip

.PHONY: clean


ajaxfileupload.war: $(zip) build/mmbase/jquery/ajaxfileupload.jspx
	mkdir -p build/mmbase/jquery/
	unzip -j $(zip) ajaxfileupload.js -d build/mmbase/jquery/
	jar cf $@ -C build .


$(zip):
	wget http://www.phpletter.com/download_project_version.php?version_id=$(versionid)


deploy: ajaxfileupload.war
	mvn deploy:deploy-file \
	 -DgeneratePom.description='JQuery Ajax FileUpload. See http://www.phpletter.com/Demo/AjaxFileUpload-Demo' -DgroupId=org.mmbase.jquery -DartifactId=ajaxfileupload   \
	 -Dversion=$(version) -Dpackaging=war \
	-Dfile=ajaxfileupload.war \
	-Durl=scp://mmbase.org/home/mmweb/web/maven2 -DrepositoryId=mmbase

build/mmbase/jquery/ajaxfileupload.jspx:
	mkdir -p build/mmbase/jquery/
	cp mmbase/jquery/ajaxfileupload.jspx  $@


clean:
	rm -rf build
	rm -rf jquery.ui.war

