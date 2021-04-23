#!/bin/sh

#Java Runtime 1.6.0
#. /home/applied/SUNJava.sh
#cd /home/applied/AiJPOS_BackEnd
cd /home/PayrollSystem
hm=`pwd`/library/*.jar
cl=$CLASSPATH
for i in ${hm}
do
  if [ -z "$PATH" ] ; then
     CLASSPATH=$i
  else
     CLASSPATH=$CLASSPATH:$i
  fi
done
CLASSPATH=.:$CLASSPATH

#echo $CLASSPATH
java -cp $CLASSPATH maintenance.bin.src.DBConn.DbaseConnectorFrame update
CLASSPATH=$cl
