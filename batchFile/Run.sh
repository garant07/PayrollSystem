#!/bin/sh
cd /home/user1/AiJPOS-BackEnd
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

echo $CLASSPATH
java -cp $CLASSPATH maintenance.bin.src.core.mainform
CLASSPATH=$cl
