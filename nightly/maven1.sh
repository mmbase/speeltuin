
DIRNAME=`dirname $0`
THIS=`(cd $DIRNAME ; pwd)`

source $THIS/env.sh
source $THIS/version.sh

cd ${MMBASE}
echo =================================MAVEN 1 ============================== |  tee -a ${builddir}/messages.log
echo all:install
((${MAVEN} all:install | tee -a ${builddir}/messages.log) 3>&1 1>&2 2>&3 | tee -a ${builddir}/errors.log) 3>&1 1>&2 2>&3
