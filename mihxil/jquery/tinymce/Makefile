version=3.2.7
version_=3_2_7

compressorversion=2.0.2
compressorversion_=2_0_2

subversion=

zip=tinymce_$(version_)_jquery.zip
compressorzip=tinymce_compressor_jsp_$(compressorversion_).zip
languagepackzip=tinymce_lang_pack.zip

.PHONY: clean



tinymce.war: $(zip) $(compressorzip)
	mkdir -p build/mmbase/
	unzip  $(zip) tinymce/jscripts/*  -d build/
	unzip  $(compressorzip)   -d build/
	unzip  $(languagepackzip)   -d build/mmbase/tiny_mce
	cp -rf build/tinymce/jscripts/* build/mmbase
	cp -rf build/tinymce_compressor_jsp/* build/mmbase/tiny_mce
	rm -rf build/tinymce
	rm -rf build/tinymce_compressor_jsp
	jar cf $@ -C build/ .


$(zip):
	wget http://downloads.sourceforge.net/project/tinymce/TinyMCE/$(version)/$(zip)?use_mirror=mesh

$(compressorzip):
	wget http://downloads.sourceforge.net/project/tinymce/TinyMCE%20Compressor%20JSP/$(compressorversion)/tinymce_compressor_jsp_$(compressorversion_).zip?use_mirror=mesh


deploy: tinymce.war
	mvn deploy:deploy-file \
	 -DgeneratePom.description='TinyMCE/jquery. Including also TinyMCE:Compressor/JSP' -DgroupId=org.mmbase.tinymce -DartifactId=tinymce-jquery   \
	 -Dversion=$(version)$(subversion) -Dpackaging=war \
	-Dfile=tinymce.war \
	-Durl=https://oss.sonatype.org/service/local/repositories/releases/content -DrepositoryId=ossrh


clean:
	rm -rf build
	rm -rf tinymce.war

