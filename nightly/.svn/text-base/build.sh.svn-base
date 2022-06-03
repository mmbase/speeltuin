#!/bin/bash

DIRNAME=`dirname $0`
THIS=`(cd $DIRNAME ; pwd)`

source $THIS/env.sh
source $THIS/version.sh



# trunk

cd ${MMBASE}
echo cwd: `pwd`, build dir: ${builddir}

# ===========CLEANING=====================================================================
echo Cleaning
echo >  ${builddir}/messages.log 2> ${builddir}/errors.log
# removes all 'target' directories
# the same as ${MAVEN} multiproject:clean >>  ${builddir}/messages.log 2>> ${builddir}/errors.log
find . -type d -name target -print | xargs rm -rf  >> ${builddir}/messages.log

# ===========SVN UP=====================================================================
pwd
echo "svn up" | tee -a ${builddir}/messages.log
${SVN}  up | tee -a ${builddir}/messages.log

echo Starting nightly build | tee -a ${builddir}/messages.log


# ===========RECENT CHANGES=====================================================================
echo creating RECENTCHANGES |  tee -a ${builddir}/messages.log
${SVN} log -r {`$THIS/dynamicdate.sh 'today-1week' 'yyyy-MM-dd'`}:HEAD > ${builddir}/RECENTCHANGES.txt


# ===========BUILDS ====================================================================


$THIS/maven2.sh


# ===========MAVEN SITE=====================================================================

cd ${MMBASE}/maven
echo Creating site `pwd`. | tee -a ${builddir}/messages.log
${MAVEN2} site-deploy |  tee -a ${builddir}/messages.log


# ================================================================================
$THIS/copy-artifacts.sh


# =====EXECUTING TESTS (using maven2 artifacts) ===========================================================================
$THIS/tests.sh


# ================================================================================
echo Creating symlink for latest build | tee -a ${builddir}/messages.log
rm $HOME/builds/latest
cd $HOME/builds
echo 'ln -s ${dir} latest' in `pwd` | tee -a ${builddir}/messages.log
ln -s ${dir} latest

$THIS/mail-results.sh
