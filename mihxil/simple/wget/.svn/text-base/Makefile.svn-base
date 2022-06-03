
.PHONY: clean upload

simple-webapp/index.html:
	wget -r -k -nH --adjust-extension http://localhost:8083/simple-webapp/
	bash -c 'for i in `find simple-webapp -name "*.html"`; do xsltproc -novalid welcomefiles.xslt $$i | xmllint --nsclean  --format - > $$i.new ; mv $$i.new $$i ; done'

deploy: simple-webapp/index.html
	(cd simple-webapp ; tar zcf es.tgz *)
	scp simple-webapp/es.tgz meeuw.org:/var/www/es/
	ssh meeuw.org  "(cd /var/www/es; tar zxf es.tgz)"


clean:
	rm -rf simple-webapp