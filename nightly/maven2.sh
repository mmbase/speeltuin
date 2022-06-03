
DIRNAME=`dirname $0`
THIS=`(cd $DIRNAME ; pwd)`

source $THIS/env.sh
source $THIS/version.sh

cd ${MMBASE}/
echo ==================================MAVEN 2 ============== |  tee -a ${builddir}/messages.log
echo -P deploy clean deploy
${MAVEN2} -P deploy clean deploy | tee -a ${builddir}/messages.log
