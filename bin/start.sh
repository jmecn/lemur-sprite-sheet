#!/bin/sh
CUR_DIR=$(cd $(dirname $0);pwd)
cd $CUR_DIR/..
java -jar -Xmx64m -XstartOnFirstThread lemur-sprite-sheet*.jar