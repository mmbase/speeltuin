
# the maven plugin seems not to be able to do sufficiently
# It'll cost me lots of time to fix the plugin or find work arounds.

# This works well, and is only a few lines.
# Make rocks...

.PHONY: clean


out:
	mvn package
	rsync -ra target/site/tlddoc/* mmbase.org:web/tags/mm/portal

