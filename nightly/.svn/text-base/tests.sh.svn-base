
DIRNAME=`dirname $0`
THIS=`(cd $DIRNAME ; pwd)`

source $THIS/env.sh
source $THIS/version.sh

echo Now executing tests. Results in ${builddir}/test-results.log | tee -a ${builddir}/messages.log
cd ${MMBASE}/tests
echo COMPILING tests | tee -a ${builddir}/messages.log
${MAVEN2} compile jar:jar | tee -a ${builddir}/messages.log
echo EXECUTING tests | tee -a ${builddir}/messages.log
${ANT} -quiet -listener org.apache.tools.ant.listener.Log4jListener -lib target/dependency:. -Dnoconnection=true run.all  2>&1 | tee  ${builddir}/tests-results.log
