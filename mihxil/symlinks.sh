#!/bin/bash
# Creates symlinks to mmbase jars in maven repository.
#
# Usage: symlinks.sh [<directory> [<taglib|crontab|...> [<taglib|crontab|..]]]
# If no directory speccified current directory is taken.
# If no mmbase applications specified it tries to explore which applications are present already, and replaces those

# To symlink didactor jars, do the following:

# michiel@belono:~/vu/didactor/webapp/WEB-INF/lib$ PROJ=didactor ~/mmbase/speeltuin/mihxil/symlinks.sh




if [ "$PROJ" == '' ]; then
    jarprefix=mmbase
    m2home=$HOME/.m2/repository/org/mmbase
elif [ "$PROJ" == 'didactor' ]; then
    jarprefix=didactor
    m2home=$HOME/.m2/repository/nl/didactor
elif [ "$PROJ" == 'media' ]; then
    jarprefix=media
    m2home=$HOME/.m2/repository/nl/vpro/media
elif [ "$PROJ" == 'image' ]; then
    jarprefix=image
    m2home=$HOME/.m2/repository/nl/vpro/image
elif [ "$PROJ" == 'user' ]; then
    jarprefix=user
    m2home=$HOME/.m2/repository/nl/vpro/user
elif [ "$PROJ" == 'magnolia' ]; then
    jarprefix=magnolia
    m2home=$HOME/.m2/repository/nl/vpro/magnolia
elif [ "$PROJ" == 'cinema' ]; then
    jarprefix=cinema
    m2home=$HOME/.m2/repository/nl/cinema
elif [ "$PROJ" == 'encoder' ]; then
    jarprefix=encoder
    m2home=$HOME/.m2/repository/nl/vpro/encoder
fi

dest=$1
shift
if [ "$dest" == "" ]; then
    dest="."
fi

if [ -z "$1" ] ; then
    apps=""
    #No explicit jars specified
    for file in `ls $dest | grep -E "$jarprefix[a-z\-]*-[0-9].*"`; do
	echo $file
	if [ $? == "0" ]; then
	    apps="$apps $file"
	else
	    echo "Could not delete for $jar"
	fi
    done
else
    apps=""
    until [ -z "$1" ]  # Until all parameters used up . . .
    do
	apps="$apps mmbase-$1";
	shift
    done
fi



for i in $apps ; do
    version=`echo ${i%.jar} | awk -F- 'BEGIN {num=0;} {for (i=1; i<=NF;i++) { if ($i ~ /[0-9].*/) {num=1;}; if (num) { printf $i; if (i < NF) printf FS;}}}'`;
    name=`echo ${i%.jar} | awk -F- 'BEGIN {num=1;} {for (i=1; i<=NF;i++) { if ($i ~ /[0-9].*/) {num=0;}; if (num) { if (i > 1) printf FS;  printf $i; }}}'`;
    src=$m2home/$name/$version/$name-$version.jar
    srcclasses=$m2home/$name/$version/$name-$version-classes.jar
    destjar=$dest/$name-$version.jar
    echo $destjar
    if [ -e $srcclasses -o -e $src ] ; then
	if [ -e $destjar -o -h $destjar ] ; then
	    rm $destjar
	fi

	if [ -e $srcclasses ] ; then
	    ln -s $srcclasses $destjar
	else
	    ln -s $src $destjar
	fi
    else
	echo No $src or $srcclasses
    fi
done
